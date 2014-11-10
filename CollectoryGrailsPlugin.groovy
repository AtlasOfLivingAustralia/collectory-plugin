import au.org.ala.custom.marshalling.DomainClassWithUidMarshaller
import grails.build.logging.GrailsConsole
import grails.converters.JSON
import grails.util.Environment

class CollectoryGrailsPlugin {
    def grailsApplication
    def dataLoaderService
    def authenticateService

    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Collectory Plugin" // Headline display name of the plugin
    def author = "Alan Lin"
    def authorEmail = "alan.lin@csiro.au"
    def description = '''\
A Grails plugin to provide the core functionality for collection and displaying biodiversity data from
collectory web services. Data access is via JSON REST web services
from the ALA collectory app (no local DB is required for this app).
'''

    // URL to the plugin's documentation
    def documentation = "http://github.com/AtlasOfLivingAustralia/collectory"

    // Extra (optional) plugin metadata


    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "MPL2"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Atlas of Living Australia", url: "http://www.ala.org.au/" ]

    // Any additional developers beyond the author specified above.
    def developers = [
            [ name: "Dave Martin", email: "david.martin@csiro.au" ],
            [ name: "Dave Baird", email: "david.baird@csiro.au" ]
    ]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "Google Code", url: "https://github.com/AtlasOfLivingAustralia/collectory/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/AtlasOfLivingAustralia/collectory" ]

    def grailsConsole =  new GrailsConsole()

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        ///*
        def config = application.config

        // EhCache settings
        if (!config.grails.cache.config) {
            config.grails.cache.config = {
                defaults {1
                    eternal false
                    overflowToDisk false
                    maxElementsInMemory 10000
                    timeToLiveSeconds 3600
                }
                //cache {
                //    name 'collectoryCache'
                //    timeToLiveSeconds (3600 * 4)
                //}
                cache {
                    name 'longTermCache'
                    timeToLiveSeconds (3600 * 12)
                }
                //cache {
                //    name 'outageCache'
                //    timeToLiveSeconds (3600 * 24 * 7)
                //}
            }
        }
        //*/

        // Apache proxyPass & cached-resources seems to mangle image URLs in plugins, so we exclude caching it
        application.config.grails.resources.mappers.hashandcache.excludes = ["**/images/*.*", "**/img/*.*", "**/theme/default/*.*"]

        ///*
        // Load the "sensible defaults"
        //println "config.skin = ${config.skin}"
        def loadConfig = new ConfigSlurper(Environment.current.name).parse(application.classLoader.loadClass("defaultConfig"))
        application.config = loadConfig.merge(config) // client app will now override the defaultConfig version
        //application.config.merge(loadConfig) //
        //println "config.security = ${config.security}"

        // Custom message source
        //messageSource(ExtendedPluginAwareResourceBundleMessageSource) {
        //    basenames = ["WEB-INF/grails-app/i18n/messages"] as String[]
        //    cacheSeconds = (60 * 60 * 6) // 6 hours
        //    useCodeAsDefaultMessage = false
        //}

        //grailsConsole.info "grails.resources.work.dir = " + config.grails.resources.work.dir

        // check custom resources cache dir for permissions
        //if (config.grails.resources.work.dir) {
        //    if (new File(config.grails.resources.work.dir).isDirectory()) {
        //        // cache dir exists - check if its writable
        //        if (!new File(config.grails.resources.work.dir).canWrite()) {
        //            grailsConsole.error "grails.resources.work.dir (${config.grails.resources.work.dir}) is NOT WRITABLE, please fix this!"
        //        }
        //    } else {
        //        // check we can create the directory
        //        if (!new File(config.grails.resources.work.dir).mkdir()) {
        //            grailsConsole.error "grails.resources.work.dir (${config.grails.resources.work.dir}) cannot be created, please fix this!"
        //        }
        //    }
        //}
        //*/
    }

    def doWithDynamicMethods = { ctx ->
    }

    def doWithApplicationContext = {ctx -> //servletContext ->
        // custom marshaller to put UID into the JSON representation of associations
        //JSON.registerObjectMarshaller( new DomainClassWithUidMarshaller(false, grailsApplication), 2)
    }

    def onChange = { event ->
    }

    def onConfigChange = { event ->
    }

    def onShutdown = { event ->
    }
}
