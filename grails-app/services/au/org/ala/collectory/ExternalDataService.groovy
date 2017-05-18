package au.org.ala.collectory

import au.org.ala.collectory.exception.ExternalResourceException
import au.org.ala.collectory.resources.DataSourceAdapter
import au.org.ala.collectory.resources.DataSourceLoad
import au.org.ala.collectory.resources.TaskPhase
import au.org.ala.collectory.resources.gbif.GbifDataSourceAdapter
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import java.util.concurrent.Executors
/**
 * Services required to collect data from various external services.
 *
 * @author Doug Palmer <doug.palmer@csiro.au>
 * @author Natasha Quimby (natasha.quimby@csiro.au)
 */
class ExternalDataService {

    def grailsApplication
    def crudService

    static final POLL_INTERVAL = 15000
    static final DateFormat ALA_TIMESTAMP_FORMAT= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    static final ADAPTORMAP = [
            [name: GbifDataSourceAdapter.SOURCE, adaptorString: GbifDataSourceAdapter.class.name]
    ]

    def CONCURRENT_LOADS = 3
    def DOWNLOAD_LIMIT = 50

    def pool = Executors.newFixedThreadPool(CONCURRENT_LOADS)
    def loadMap = [:]

    def getPublishingCountriesMap(){
        def js = new JsonSlurper()
        def jsonText = new URL(grailsApplication.config.gbifApiUrl + "/node/country").text
        def isoCodeList = js.parseText(jsonText)
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
        pubMap
    }

    /**
     * Returns the status information for a specific losd
     *
     * @param guid The load guid
     *
     * @return
     */
    def getStatusInfoFor(String guid){
        return loadMap[guid]
    }

    /**
     * Look for some external resources
     *
     * @param dp The default data provider
     * @param country The country
     * @param userLimit The maximum number of resources
     *
     * @return A list of external resource descriptions, possibly mapped against
     */
    def searchForDatasets(DataSourceConfiguration configuration) {
        def adaptor = configuration.createAdaptor()
        List<Map> datasets = adaptor.datasets()
        List<ExternalResourceBean> resources = datasets.collect { adaptor.createExternalResource(it) }
        return resources.sort()
     }

    /**
     * Update the collectory with data from external resources
     *
     * @param configuration
     * @param loadGuid
     *
     */
    def updateFromExternalSources(DataSourceConfiguration configuration, String loadGuid) {
        DataSourceLoad load = new DataSourceLoad(guid: loadGuid, startTime: new Date(), configuration: configuration)
        DataSourceAdapter adaptor = load.configuration.createAdaptor()
        loadMap[loadGuid] = load
        load.resources.each { resource ->
            resource.phase = TaskPhase.NEW
            processResourceMetadata(adaptor, load, resource)
        }
        load.resources.each { resource ->
            if (!resource.phase.terminal) {
                resource.phase = TaskPhase.QUEUED
                pool.submit({ processResource(load, resource) } as Callable)
            }
        }
        pool.submit({ processLoad(load) } as Callable)
    }

    /**
     * Process that waits on a data load for completion.
     *
     * @param load The load to process
     */
    def processLoad(DataSourceLoad load) {
        try {
            while (!load.isComplete()) {
                Thread.sleep(POLL_INTERVAL)
            }
            load.finishTime = new Date()
            Thread.sleep(POLL_INTERVAL * 4)
        } finally {
            loadMap.remove(load.guid)
        }
    }

    /**
     * Update any metadata on the resource.
     * <p>
     * Done sequentially to avoid transaction problems leading to race conditions.
     */
    def processResourceMetadata(DataSourceAdapter adaptor, DataSourceLoad load, ExternalResourceBean resource) {
        try {
            if (resource.phase.terminal) return // Cancelled externally

            // Before any other updates we must link the external identifier, if there isn't one available
            resource.phase = TaskPhase.METADATA
            DataResource dr = null
            ExternalIdentifier ext = null
            if (resource.uid) {
                dr = DataResource.findByUid(resource.uid)
                if (!dr) {
                    throw new ExternalResourceException("Can't find resource", "manage.note.note12", resource.uid)
                }
                ext = dr.getExternalIdentifiers().find({
                    it.source == adaptor.source && it.identifier == resource.guid
                })
                if (dr && !ext) {
                    dr.addExternalIdentifier(resource.guid, adaptor.source, resource.source)
                }
            }

            if (!resource.updateRequired) {
                resource.phase = (dr && !ext) ? TaskPhase.COMPLETED : TaskPhase.IGNORED
                return
            }

            def update = adaptor.getDataset(resource.guid)
            if (load.configuration.dataProviderUid)
                update.dataProvider = [uid: load.configuration.dataProviderUid]
            update = convertToJSON(update)
            if (dr && update && resource.updateMetadata) {
                dr = crudService.updateDataResource(dr, update)
            }
            if (!dr && resource.addResource) {
                dr = crudService.insertDataResource(update)
                if (!dr) {
                    throw new ExternalResourceException("Can't create resource", "manage.note.note01", resource.name, resource.guid)
                }
                if (dr.hasErrors()) {
                    throw new ExternalResourceException("Created resoruce has errors", "manage.note.note02", resource.name, dr.errors)
                }
                dr.addExternalIdentifier(resource.guid, adaptor.source, resource.source)
                resource.uid = dr.uid
                resource.addNote("manage.note.note03", resource.uid)
            }
            if (!dr || !resource.updateConnection) {
                resource.phase = TaskPhase.COMPLETED
            }
        } catch (ExternalResourceException ex) {
            log.error("Unable to process resource ${resource} ${ex.class}", ex)
            resource.addError(ex.code, ex.args)
        } catch (Exception ex) {
            log.error("Unable to process resource ${resource} ${ex.class}", ex)
            resource.addError("manage.note.note05", ex.message ?: ex.class.name)
        }

    }

    /**
     * Load a single external resource
     *
     * @param load The load process
     * @param resource The resource to load
     */
    def processResource(DataSourceLoad load, ExternalResourceBean resource) {
        try {
            if (resource.phase.terminal) return // Cancelled externally

            Thread.sleep(100) // Allow transaction to complete
            def adaptor = load.configuration.createAdaptor()
            DataResource dr = null
            DataResource.withTransaction {
                dr = DataResource.findByUid(resource.uid)
            }

            if (!dr) {
                throw new ExternalResourceException("Can't find resource", "manage.note.note12", resource.uid)
            }

            if (!resource.updateConnection) {
                resource.phase = TaskPhase.COMPLETED
                return
            }

            // See if we have any data
            def hasData = adaptor.isDataAvailableForResource(resource.guid)
            if (!hasData) {
                resource.phase = TaskPhase.EMPTY
                return
            }
            if (resource.phase.terminal) return // Cancelled externally

            if (adaptor.isGeneratable()) {
                resource.phase = TaskPhase.GENERATING
                resource.occurrenceId = adaptor.generateData(resource.guid)
                if (resource.phase.terminal) return // Cancelled externally

                TaskPhase status = TaskPhase.GENERATING
                while (!status.terminal && !resource.phase.terminal) {
                    status = adaptor.generateStatus(resource.occurrenceId)
                    if (!status.terminal) {
                        Thread.sleep(POLL_INTERVAL)
                    }
                }
                if (resource.phase.terminal) return // Cancelled externally
                if (status != TaskPhase.COMPLETED) {
                    throw new ExternalResourceException("Unable to generate occurrence data", "manage.note.note04", status)
                }
            }

            File uploadFileName = null
            if (adaptor.isDownloadable()) {
                resource.phase = TaskPhase.DOWNLOADING
                File uploadDir = new File(grailsApplication.config.uploadFilePath as String)
                File uploadTmpDir = new File(new File(uploadDir, "tmp"), resource.occurrenceId);
                FileUtils.forceMkdir(uploadTmpDir)
                File tmpFileName = new File(uploadTmpDir, resource.occurrenceId);
                adaptor.downloadData(resource.occurrenceId, tmpFileName)
                if (resource.phase.terminal) return // Cancelled externally

                resource.phase = TaskPhase.PROCESSING
                String fileId = Long.toString(System.currentTimeMillis())
                File uploadWorkDir = new File(uploadDir, fileId)
                FileUtils.forceMkdir(uploadWorkDir)
                uploadFileName = adaptor.processData(tmpFileName, uploadWorkDir, resource)
                if (resource.phase.terminal) return // Cancelled externally
            }

            resource.phase = TaskPhase.CONNECITNG
            def connection = (new JsonSlurper()).parseText(dr.connectionParameters ?: '{}')
            def update = convertToJSON(adaptor.buildConnection(uploadFileName, connection, resource))
            DataResource.withTransaction {
                crudService.updateDataResource(dr, update)
            }

            resource.phase = TaskPhase.COMPLETED
        } catch (ExternalResourceException ex) {
            log.error("Unable to process resource ${resource} ${ex.class}", ex)
            resource.addError(ex.code, ex.args)
        } catch (Exception ex) {
            log.error("Unable to process resource ${resource} ${ex.class}", ex)
            resource.addError("manage.note.note05", ex.message ?: ex.class.name)
        }
    }

    /**
     * Deep convert of a map to a JSONObject.
     * <p>
     * Needed by the {@link #crudService} which expects JSON from a web service call
     *
     * @param o The map
     *
     * @return The mapped object
     */
    Object convertToJSON(Object o) {
        if (o == null)
            return o;
        if (o instanceof Map) {
            def json = new JSONObject()
            o.each { key, value ->
                json.put(key, convertToJSON(value))
            }
            return json
        }
        if (o instanceof Collection) {
            def json = new JSONArray()
            o.each { value ->
                json.put(convertToJSON(value))
            }
        }
        if (o instanceof Date)
            return ALA_TIMESTAMP_FORMAT.clone().format(o)
        return o
    }

}
