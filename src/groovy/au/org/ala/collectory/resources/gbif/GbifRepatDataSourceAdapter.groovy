package au.org.ala.collectory.resources.gbif

import au.org.ala.collectory.DataResource
import au.org.ala.collectory.DataSourceConfiguration
import au.org.ala.collectory.ExternalResourceBean
import au.org.ala.collectory.GbifService
import au.org.ala.collectory.Licence
import au.org.ala.collectory.exception.ExternalResourceException
import au.org.ala.collectory.resources.TaskPhase
import groovy.json.JsonOutput
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import net.sf.json.JSONObject
import org.apache.http.HttpEntity
import org.apache.http.HttpHeaders
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory

import java.text.MessageFormat
import java.text.ParseException

/**
 * Data source adapters for the GBIF API
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class GbifRepatDataSourceAdapter extends GbifDataSourceAdapter {

    static final LOGGER = LoggerFactory.getLogger(GbifRepatDataSourceAdapter.class)
    static final SOURCE = "GBIF_REPATRIATION"
    GbifService gbifService

    static final String OCCURRENCE_REPAT_SEARCH = "occurrence/search?repatriated=true&country={0}&type={1}&offset=0&limit=0&facet=datasetKey&facetLimit={2}"

    GbifRepatDataSourceAdapter(DataSourceConfiguration configuration) {
        super(configuration)
     }

    @Override
    List<Map> datasets() throws ExternalResourceException {
        def keys = []
        def datasets = []

        LOGGER.info("Requesting dataset lists configuration.country: ${configuration.country}")
        String url = MessageFormat.format(OCCURRENCE_REPAT_SEARCH, configuration.country,configuration.recordType, configuration.maxNoOfDatasets)
        JSONObject json = getJSONWS(url)
        if (json?.facets) {
            json.facets[0].counts.each {
                keys << it.name

                if (it.count >= configuration.minRecordCount && it.count <= configuration.maxRecordCount) {
                    LOGGER.info("Getting metadata for ${it.name}  = ${it.count}")
                    def dataset = getDataset(it.name, it.count)
                    if (dataset.name) {
                        datasets << dataset
                    }
                } else {
                    LOGGER.info("Skipping dataset ${it.name}  = ${it.count}")
                }
            }
        }

        LOGGER.info("Total datasets retrieved: " + datasets.size())
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
        ext.recordCount = external.recordCount
        return ext
     }

    Map getDataset(String id, Integer recordCount) throws ExternalResourceException {
        Map dataset= getJSONWS(MessageFormat.format("dataset/{0}", id))
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

        [
                name: dataset.title,
                acronym: dataset.abbreviation,
                guid: dataset.key,
                address: address,
                phone: phone,
                email: email,
                pubDescription: dataset.description,
                state: address?.state,
                websiteUrl: dataset.homepage,
                rights: dataset.rights,
                licenseType: license?.licenseType,
                licenseVersion: license?.licenseVersion,
                citation: dataset.citation,
                resourceType: recordType,
                contentTypes: JsonOutput.toJson(contentTypes),
                dataCurrency: currency,
                lastChecked: new Date(),
                source: source,
                gbifDoi: dataset.doi,
                gbifRegistryKey: dataset.key,
                gbifDataset: true,
                isShareableWithGBIF: false,
                recordCount: recordCount
        ]
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
                creator: configuration.username,
                notification_address: [],
                format: "DWCA",
                predicate: [
                    type: "and",
                    predicates: [
                        [
                            type : "equals",
                            key  : "DATASET_KEY",
                            value: guid
                        ],
                        [
                            type : "equals",
                            key: "COUNTRY",
                            value: configuration.country
                        ]
                    ]
                ]
        ]

        StringEntity requestEntity = new StringEntity(
                JsonOutput.toJson(request),
                "application/json",
                "UTF-8")

        String encoding = Base64.getEncoder()
                .encodeToString((configuration.username + ":" + configuration.password).getBytes())
        HttpClient httpClient = HttpClientBuilder.create()
                .build();

        HttpPost httpPost = new HttpPost(new URL(configuration.endpoint, "occurrence/download/request").toURI())
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
        httpPost.setEntity(requestEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        return IOUtils.readLines(entity.getContent()).get(0)
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
        HTTPBuilder http = new HTTPBuilder(new URL(configuration.endpoint, path))
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
