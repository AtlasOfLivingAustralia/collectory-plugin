package au.org.ala.collectory.resources.gbif

import au.org.ala.collectory.DataResource
import au.org.ala.collectory.DataSourceConfiguration
import au.org.ala.collectory.ExternalResourceBean
import au.org.ala.collectory.ProviderGroup
import au.org.ala.collectory.exception.ExternalResourceException
import au.org.ala.collectory.resources.DataSourceAdapter
import au.org.ala.collectory.resources.TaskPhase
import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import net.sf.json.JSONObject
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory

import java.text.DateFormat
import java.text.MessageFormat
import java.text.ParseException
import java.text.SimpleDateFormat
/**
 * Data source adapters for the GBIF API
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class GbifDataSourceAdapter extends DataSourceAdapter {
    static final LOGGER = LoggerFactory.getLogger(GbifDataSourceAdapter.class)
    static final SOURCE = "GBIF"
    static final String COUNTRIES = "node/country"
    static final MessageFormat DATASET_SEARCH = new MessageFormat("dataset/search?publishingCountry={0}&type={1}&offset={2}&limit={3}")
    static final MessageFormat DATASET_GET = new MessageFormat("dataset/{0}")
    static final String OCCURRENCE_DOWNLOAD_REQUEST = "occurrence/download/request"
    static final MessageFormat DATASET_RECORD_COUNT = new MessageFormat("occurrence/count?datasetKey={0}")
    static final MessageFormat DOWNLOAD_STATUS = new MessageFormat("occurrence/download/{0}")
    static final MessageFormat OCCURRENCE_DOWNLOAD = new MessageFormat("occurrence/download/request/{0}.zip")
    static final DateFormat TIMESTAMP_FORMAT= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    static LICENSE_MAP = [
            "https://creativecommons.org/publicdomain/zero/1.0/legalcode": [licenseType: "CC0", licenseVersion: "1.0" ],
            "https://creativecommons.org/licenses/by-nc/4.0/legalcode":    [licenseType: "CC BY-NC", licenseVersion: "4.0" ],
            "https://creativecommons.org/licenses/by/4.0/legalcode":       [licenseType: "CC BY", licenseVersion: "4.0" ]
    ]
    static TYPE_MAP = [
            "CHECKLIST"    : "species-list",
            "METADATA"     : "document",
            "OCCURRENCE"   : "records",
            "SAPLING_EVENT": "records"
    ]
    static DATASET_TYPES = [
            "OCCURRENCE"   : "Occurrence Records"  // We only allow occurrence records at the moment
    ]
    static CONTENT_MAP = [
            "CHECKLIST"    : ["species list", "taxonomy", "gbif import"],
            "METADATA"     : ["gbif import"],
            "OCCURRENCE"   : ["point occurrence data", "gbif import"],
            "SAPLING_EVENT": ["point occurrence data", "gbif import"]
    ]
    static DOWNLOAD_STATUS_MAP = [
            "CANCELLED" : TaskPhase.CANCELLED,
            "FAILED"    : TaskPhase.ERROR,
            "KILLED"    : TaskPhase.CANCELLED,
            "PREPARING" : TaskPhase.GENERATING,
            "RUNNING"   : TaskPhase.GENERATING,
            "SUCCEEDED" : TaskPhase.COMPLETED,
            "SUSPENDED" : TaskPhase.GENERATING,
            "UNKNOWN"   : TaskPhase.ERROR
    ]

    int pageSize = 20

    GbifDataSourceAdapter(DataSourceConfiguration configuration) {
        super(configuration)
     }

    /**
     * Get the source label for external identifiers
     *
     * @return {@link #SOURCE}
     */
    @Override
    String getSource() {
        return SOURCE
    }

    @Override
    Map getCountryMap(){
        def isoCodeList = getJSONWS(COUNTRIES)
        //intersect with iso names
        def isoMap = [:]
        this.class.classLoader.getResourceAsStream("isoCodes.csv").readLines().each{
            def codeAndName = it.split("\t")
            isoMap.put(codeAndName[0], codeAndName[1])
        }
        def pubMap = [:]
        isoCodeList.each {
            def name = isoMap.get(it)
            pubMap.put(it, name)
        }
        return pubMap
    }

    @Override
    Map getDatasetTypeMap() {
        return DATASET_TYPES
    }


    @Override
    List<Map> datasets() throws ExternalResourceException {
        int offset = 0
        def keys = []
        def datasets = []
        boolean atEnd = false
         while (!atEnd) {
            JSONObject json = getJSONWS(DATASET_SEARCH.format([configuration.country, configuration.recordType, offset, pageSize].toArray()))
            if (json?.results) {
                json.results.each { keys << it.key }
                offset += json.results.size()
            }
            atEnd = !json || !json.results || json.endOfRecords
        }
        keys.each {
            def dr = getDataset(it)
            if (dr) {
                datasets << dr
            }
        }
        return datasets
    }

    @Override
    ExternalResourceBean createExternalResource(Map external) {
        ExternalResourceBean ext = new ExternalResourceBean(name: external.name, guid: external.guid, source: external.source, sourceUpdated: external.dataCurrency)
        DataResource dr = ext.resolve(source)
        if (!dr) {
            ext.status = ExternalResourceBean.ResourceStatus.NEW
            ext.addResource = true
            ext.updateMetadata = false
            ext.updateConnection = true
        } else {
            ext.uid = dr.uid
            ext.existingChecked = dr.lastChecked ? new Date(dr.lastChecked.time) : null
            if (!dr.gbifDataset) {
                ext.status = ExternalResourceBean.ResourceStatus.LOCAL
                ext.addResource = false
                ext.updateMetadata = false
                ext.updateConnection = false
            }  else if (ext.existingChecked == null || ext.sourceUpdated == null || ext.existingChecked.before(ext.sourceUpdated)) {
                ext.status = ExternalResourceBean.ResourceStatus.CHANGED
                ext.addResource = false
                ext.updateMetadata = true
                ext.updateConnection = true
            } else {
                ext.status = ExternalResourceBean.ResourceStatus.UNCHANGED
                ext.addResource = false
                ext.updateMetadata = false
                ext.updateConnection = false
            }
        }
        return ext
     }

    @Override
    Map getDataset(String id) throws ExternalResourceException {
        JSONObject json = getJSONWS(DATASET_GET.format([ id ].toArray()))
        return json ? translate(json) : null
    }

    Map translate(JSONObject dataset) {
        def currency = null
        def originator = dataset.contacts?.find { it.type == "ORIGINATOR" }
        def address = originator == null ? null : [
                street: originator.address?.join(" "),
                city: originator.city,
                state: originator.province,
                postcode: originator.postalCode,
                country: originator.country
        ]
        def phone = originator?.phone ? originator.phone.first() : null
        def email = originator?.email ? originator?.email?.first() : null
        def license = LICENSE_MAP[dataset.license]
        def recordType = TYPE_MAP[dataset.type]
        def contentTypes = CONTENT_MAP[dataset.type] ?: []
        def source = dataset.doi?.replace("doi:", "https://doi.org/")
        try {
            currency = dataset.pubDate ? TIMESTAMP_FORMAT.clone().parse(dataset.pubDate) : null
        } catch (ParseException ex) {
        }
        def resource = [
                name: dataset.title,
                acronym: dataset.abbreviation,
                guid: dataset.key,
                address: address,
                phone: phone,
                email: email,
                pubDescription: dataset.description,
                state: address?.state && ProviderGroup.statesList.contains(address.state) ? address.state : null,
                websiteUrl: dataset.homepage,
                rights: dataset.rights,
                licenseType: license?.licenseType,
                licenseVersion: license?.licenseVersion,
                citation: dataset.citation?.identifier ? dataset.citation.identifier : dataset.citation?.text,
                resourceType: recordType,
                contentTypes: JsonOutput.toJson(contentTypes),
                dataCurrency: currency,
                lastChecked: new Date(),
                source: source,
                gbifDoi: dataset.doi,
                gbifRegistryKey: dataset.key,
                gbifDataset: true
        ]
        addDefaultDatasetValues(resource)
        return resource
    }

    /**
     * Check to see if data is available for the supplied resource ID.
     * @param guid The resource GUID
     *
     * @return True if there is data available
     */
    @Override
    boolean isDataAvailableForResource(String guid) throws ExternalResourceException {
        def http = new HTTPBuilder(new URL(configuration.endpoint, DATASET_RECORD_COUNT.format([guid].toArray())))
        def count = null
        if (configuration.username) {
            http.auth.basic(configuration.username, configuration.password)
        }
        http.request(Method.GET, ContentType.TEXT) { req ->
            headers.Accept = ContentType.ANY.acceptHeader
            response.failure = { resp ->
                throw new ExternalResourceException("Unable check for data", "manage.note.note11", guid, resp.statusLine)
            }
            response.success = { resp, responseBody ->
                count = responseBody.text
            }
        }
        return count && count.toInteger() > 0
    }

    /**
     * Starts the GBIF download by calling the API.
     * <p>
     * The GBIF API is difficult at this point. It returns a text string containing a string download Id
     * but claims that the content type is application/json
     *
     * @param guid The GBIF identifier for the resource
     * @return The downloadId used to monitor when the download has been completed
     */
    @Override
    String generateData(String guid) throws ExternalResourceException {
        def request = [
                creator             : configuration.username,
                notification_address: [],
                predicate           : [
                        type : "equals",
                        key  : "DATASET_KEY",
                        value: guid
                ]
        ]
        LOGGER.debug("Sending download request for ${guid}")
        def http = new HTTPBuilder(new URL(configuration.endpoint, OCCURRENCE_DOWNLOAD_REQUEST))
        def downloadId = null
        if (configuration.username) {
            http.auth.basic(configuration.username, configuration.password)
        }
        http.request(Method.POST, ContentType.TEXT) { req ->
            requestContentType = ContentType.JSON
            headers.Accept = ContentType.JSON.acceptHeader
            body = request
            response.failure = { resp ->
                throw new ExternalResourceException("Unable to generate download", "manage.note.note06", resp.statusLine)
            }
            response.success = { resp, responseBody ->
                downloadId = responseBody.text
            }
        }
        return downloadId
    }

    /**
     * Check to see how the download is coming along
     *
     * @param id The download id
     *
     * @return Either completed, generating or an error status
     */
    @Override
    TaskPhase generateStatus(String id) throws ExternalResourceException {
        def status = getJSONWS(DOWNLOAD_STATUS.format([id].toArray()))
        LOGGER.debug("Download status for ${id}: ${status}")
        return DOWNLOAD_STATUS_MAP[status?.status] ?: TaskPhase.GENERATING
    }

    /**
     * Collect the generated data from the GBIF server
     *
     * @param id The download id
     * @param target The target file
     */
    @Override
    void downloadData(String id, File target) throws ExternalResourceException {
        def status = getJSONWS(DOWNLOAD_STATUS.format([id].toArray()))
        if (DOWNLOAD_STATUS_MAP[status?.status] != TaskPhase.COMPLETED) {
            throw new ExternalResourceException("Expecting completed generation", "manage.note.note07", status.status)
        }
        def link = status.downloadLink
        def http = new HTTPBuilder(link)
        def is = null
        http.handler.failure = { resp ->
            throw new ExternalResourceException("Unable to retrieve ${link}", "manage.note.note08", link, resp.statusLine)
        }
        if (configuration.username) {
            http.auth.basic(configuration.username, configuration.password)
        }
        http.get(contentType: ContentType.BINARY) { resp, responseBody ->
            FileOutputStream os = new FileOutputStream(target)
            try {
                IOUtils.copy(responseBody, os)
            } finally {
                IOUtils.closeQuietly(os);
            }
        }
    }

    /**
     * Processing simply involves moving the DwCA into the work directory
     *
     * @param downloaded The downloaded data
     * @param workDir The work directory
     * @param resource The external resource description
     *
     * @return The resulting processed file
     */
    @Override
    File processData(File downloaded, File workDir, ExternalResourceBean resource) throws ExternalResourceException {
        try {
            File upload = new File(workDir, resource.occurrenceId + "-dwca.zip")
            FileUtils.moveFile(downloaded, upload)
            return upload
        } catch (IOException ex) {
            throw new ExternalResourceException("Unable to process download", ex, "manage.note.note09", downloaded)
        }
    }

    /**
     * Return a DataResource update with suitable connection parameters, etc.
     *
     * @param upload The file to upload
     * @param connection The existing connection parameters
     * @param resource The external resource
     *
     * @return A suitable update
     */
    @Override
    Object buildConnection(File upload, Object connection, ExternalResourceBean resource) throws ExternalResourceException {
        def update = [:]
        connection.url = "file:///${upload.absolutePath}"
        connection.protocol = "DwCA"
        connection.termsForUniqueKey = ["gbifID"]
        update.connectionParameters = (new JsonOutput()).toJson(connection)
        return update
    }

    /**
     * Uses a HTTP "GET" to return the JSON output of the supplied url
     *
     * @param url The request URL, relative to the configuration endpoint
     *
     * @return A JSON response
     */
    def getJSONWS(String path) throws ExternalResourceException {
        def http = new HTTPBuilder(new URL(configuration.endpoint, path))
        if (configuration.username) {
            http.auth.basic(configuration.username, configuration.password)
        }
        http.request(Method.GET, ContentType.JSON) { req ->
            requestContentType = ContentType.JSON
            response.failure = { resp ->
                throw new ExternalResourceException("Unable to get ${http.uri} response ${resp.statusLine}", "manage.note.note10", http.uri, resp.statusLine)
            }
            response.success = { resp, responseBody ->
                return responseBody
            }
        }
    }
}
