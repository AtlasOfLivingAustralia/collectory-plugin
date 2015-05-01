package au.org.ala.collectory

import grails.transaction.Transactional
import groovyx.net.http.HTTPBuilder
import groovy.util.slurpersupport.GPathResult

import javax.activation.DataSource
import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Collect datasets from an IPT service and update the
 * <p>
 * The IPT service is associated with a {@link DataProvider} that identifies the sources of the service.
 * When invoked, the service scans the RSS feed supplied by the service and uses it to identify new and
 * updated {@link DataResource}s. New datasets are collected and then supplied to the collectory for
 * loading.
 *
 * @see <a href="http://code.google.com/p/gbif-providertoolkit/">GBIF Provider Toolkit</a>
 */
class IptService {

    static transactional = true
    def grailsApplication
    def idGeneratorService
    def dataLoaderService

    /** The standard IPT service namespace for XML documents */
    static final NAMESPACES = [
            ipt:"http://ipt.gbif.org/",
            dc:"http://purl.org/dc/elements/1.1/",
            foaf:"http://xmlns.com/foaf/0.1/",
            geo:"http://www.w3.org/2003/01/geo/wgs84_pos#",
            eml:"eml://ecoinformatics.org/eml-2.1.1"
    ]
    /** Source of the RSS feed */
    static final RSS_PATH = "rss.do"
    /** The form that an IPT resource reference takes */
    //static final IPT_RESOURCE_PATTERN = /^.+\/resource.do\?r=([A-Za-z0-9_]+)$/
    /** Parse RFC 822 date/times */
    static final RFC822_PARSER = new SimpleDateFormat('EEE, d MMM yyyy HH:mm:ss Z')
    /** Parse ISO 8601 date/times */
    //static final ISO8601_PARSER = new SimpleDateFormat('yyyy-MM-dd\'T\'HH:mm:ssXXX')


    /** Fields that we can derive from the RSS feed */
    protected rssFields = [
            guid: { item -> item.link.text() },
            name: { item -> item.title.text() },
            pubDescription: { item -> item.description.text() },
            websiteUrl: { item -> item.link.text() },
            dataCurrency: { item ->
                def pd = item.pubDate?.text()

                pd == null || pd.isEmpty() ? null : new Timestamp(RFC822_PARSER.parse(pd).getTime())
            },
            lastChecked: { item -> new Timestamp(System.currentTimeMillis()) },
            provenance: { item -> "Published dataset" },
            contentTypes: { item -> "[ \"point occurrence data\" ]" }
    ]
    /** Fields that we can derive from the EML document */
    protected emlFields = [
            pubDescription: { eml -> this.collectParas(eml.dataset.abstract?.para) },
            rights: { eml ->  this.collectParas(eml.dataset.intellectualRights?.para) },
            citation: { eml ->  eml.additionalMetadata?.metadata?.gbif?.citation?.text() },
            state: { eml ->
                def state = eml.dataset.contact?.address?.administrativeArea?.text()

                if (state)
                    state = this.dataLoaderService.massageState(state)
                ProviderGroup.statesList.contains(state) ? state : null
            },
            email: { eml ->  eml.dataset.contact?.electronicMailAddress?.text() },
            phone: { eml ->  eml.dataset.contact?.phone?.text() }
    ]
    /** All field names */
    protected allFields = rssFields.keySet() + emlFields.keySet()

    /** Collect individual XML para elements together into a single block of text */
    protected def collectParas(GPathResult paras) {
        paras?.list().inject(null, { acc, para -> acc == null ? (para.text()?.trim() ?: "") : acc + " " + (para.text()?.trim() ?: "") })
    }

    /**
     * See what needs updating for a data provider.
     *
     * @param provider The provider
     * @param create Update existing datasets and create data resources for new datasets
     * @param check Check to see whether a resource needs updating by looking at the data currency
     * @param keyName The term to use as a key when creating new resources
     *
     * @return A list of data resources that need re-loading
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRED)
    def scan(DataProvider provider, boolean create, boolean check, String keyName, String username, boolean admin) {
        ActivityLog.log username, admin, provider.uid, Action.SCAN
        def updates = this.rss(provider, keyName)

        return merge(provider, updates, create, check, username, admin)
    }

    /**
     * Merge the datasets known to the dataprovider with a list of updates.
     *
     * @param provider The provider
     * @param updates The updates
     * @param create Update existing data resources and create any new resources
     * @param check Check against existing data for currency
     *
     * @return A list of merged data resources
     */
    @org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRED)
    def merge(DataProvider provider, List<DataResource> updates, boolean create, boolean check, String username, boolean admin) {
        def current = provider.resources.inject([:], { map, item -> map[item.websiteUrl] = item; map })
        def merged = []

        for (update in updates) {
            DataResource old = current[update.websiteUrl]

            if (old)  {
                if (!check || old.dataCurrency == null || update.dataCurrency == null || update.dataCurrency.after(old.dataCurrency)) {
                    for (name in allFields) {
                        def val = update.getProperty(name)

                        if (val != null)
                            old.setProperty(name, val)
                    }
                    old.userLastModified = username
                    if (create) {
                        old.save(flush: true)
                        //old.errors.each { println it.toString() }
                        ActivityLog.log username, admin, Action.EDIT_SAVE, "Updated IPT data resource " + old.uid + " from scan"
                    }
                    merged << old
                }
            } else {
                if (create) {
                    update.uid = idGeneratorService.getNextDataResourceId()
                    update.userLastModified = username
                    update.save(flush: true)
                    //update.errors.each { println it.toString() }
                    ActivityLog.log username, admin, Action.CREATE, "Created new IPT data resource for provider " + provider.uid  + " with uid " + update.uid + " for dataset " + update.websiteUrl
                }
                merged << update
            }
        }
        return merged
    }

    /**
     * Scan an IPT data provider's RSS stream and build a set of datasets.
     * <p>
     * The data sets are not saved, unless the need to create a new dataset comes into play
     *
     * @param provider The provider
     * @param keyName The term to use as a key when creating new resources
     *
     * @return A list of (possibly new providers)
     */
    def rss(DataProvider provider, String keyName) {

        def url = provider.websiteUrl
        if(!url.endsWith("/")){
            url = url + "/"
        }

        def base = new URL(url)
        def rsspath = new URL(base, RSS_PATH)
        log.info("Scanning ${rsspath} from ${base}")
        def rss = new HTTPBuilder(rsspath).get([:])
        rss.declareNamespace(NAMESPACES)
        def items = rss.channel.item

        return items.collect { item -> this.createDataResource(provider, item, keyName) }
    }

    /**
     * Construct from an RSS item
     *
     * @param provider The data provider
     * @param rssItem The RSS item
     * @param keyName The term to use as a key when creating new resources
     *
     * @return A created resource matching the information provided
     */
    def createDataResource(DataProvider provider, GPathResult rssItem, String keyName) {
        def resource = new DataResource()
        def eml = rssItem."ipt:eml"?.text()
        def dwca = rssItem."ipt:dwca"?.text()

        resource.dataProvider = provider
        rssFields.each { name, accessor -> resource.setProperty(name, accessor(rssItem))}
        resource.connectionParameters =  dwca == null || dwca.isEmpty() ? null : "{ \"protocol\": \"DwCA\", \"url\": \"${dwca}\", \"automation\": true, \"termsForUniqueKey\": [ \"${keyName}\" ] }";
        if (eml != null)
            retrieveEml(resource, eml)
        return resource
    }

    /**
     * Retreieve Eco-informatics metadata for the dataset and put it into the resource description.
     *
     * @param resource The resource
     * @param url The URL of the metadata
     *
     */
    def retrieveEml(DataResource resource, String url) {
        log.debug("Retrieving EML from " + url)
        def http = new HTTPBuilder(url)
        def eml = http.get([:]).declareNamespace(NAMESPACES)

        emlFields.each { name, accessor ->
            def val = accessor(eml)
            if (val != null)
                resource.setProperty(name, val)
        }
    }
}