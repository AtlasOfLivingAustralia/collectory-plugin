///******************************************************************************\
// *  CONFIG MANAGEMENT
// \******************************************************************************/
//def appName = 'collectory'
//def ENV_NAME = "${appName.toUpperCase()}_CONFIG"
//default_config = "/data/${appName}/config/${appName}-config.properties"
//if(!grails.config.locations || !(grails.config.locations instanceof List)) {
//    grails.config.locations = []
//}
//
//if(System.getenv(ENV_NAME) && new File(System.getenv(ENV_NAME)).exists()) {
//    println "[${appName}] Including configuration file specified in environment: " + System.getenv(ENV_NAME);
//    grails.config.locations.add "file:" + System.getenv(ENV_NAME)
//} else if(System.getProperty(ENV_NAME) && new File(System.getProperty(ENV_NAME)).exists()) {
//    println "[${appName}] Including configuration file specified on command line: " + System.getProperty(ENV_NAME);
//    grails.config.locations.add "file:" + System.getProperty(ENV_NAME)
//} else if(new File(default_config).exists()) {
//    println "[${appName}] Including default configuration file: " + default_config;
//    grails.config.locations.add "file:" + default_config
//} else {
//    println "[${appName}] No external configuration file defined."
//}
//
//println "[${appName}] (*) grails.config.locations = ${grails.config.locations}"
//println "default_config = ${default_config}"
//
//
///******************************************************************************\
// *  SKINNING
// \******************************************************************************/
//if (!ala.skin) {
//    ala.skin = 'ala2'
////    ala.skin = 'generic'
//}
//if (!skin.orgNameLong) {
//    skin.orgNameLong = "Atlas of Living Australia"
//}
//if (!skin.orgNameShort) {
//    skin.orgNameShort = "ALA"
//}
//if (!skin.includeBaseUrl) {
//    // whether crumb trail should include a home link that is external to this webabpp - ala.baseUrl is used if true
//    skin.includeBaseUrl = true
//}
//if (!skin.headerUrl) {
//    skin.headerUrl = "classpath:resources/generic-header.jsp" // can be external URL
//}
//if (!skin.footerUrl) {
//    skin.footerUrl = "classpath:resources/generic-footer.jsp" // can be external URL
//}
///******************************************************************************\
// *  EXTERNAL SERVERS
//\******************************************************************************/
//if (!bie.baseURL) {
//     bie.baseURL = "http://bie.ala.org.au/"
//}
//if (!bie.searchPath) {
//    bie.searchPath = "/search"
//}
//if (!biocacheUiURL) {
//    biocacheUiURL = "http://biocache.ala.org.au"
//}
//if(!biocacheServicesUrl){
//    biocacheServicesUrl = "http://biocache.ala.org.au/ws"
//}
//if (!spatial.baseURL) {
//    spatial.baseURL = "http://spatial.ala.org.au/"
//}
//if (!ala.baseURL) {
//    ala.baseURL = "http://www.ala.org.au"
//}
//if (!headerAndFooter.baseURL) {
//    headerAndFooter.baseURL = "http://www2.ala.org.au/commonui"
//}
//if(!alertUrl){
//    alertUrl = "http://alerts.ala.org.au/"
//}
//if(!speciesListToolUrl){
//    speciesListToolUrl = "http://lists.ala.org.au/speciesListItem/list/"
//}
//
//if(!alertResourceName){
//    alertResourceName = "Atlas"
//}
//if(!uploadFilePath){
//    uploadFilePath = "/data/collectory/upload/"
//}
//if(!uploadExternalUrlPath){
//    uploadExternalUrlPath = "/upload/"
//}
///******************************************************************************\
// *  RELOADABLE CONFIG
//\******************************************************************************/
////reloadable.cfgPollingFrequency = 1000 * 60 * 60 // 1 hour
////reloadable.cfgPollingRetryAttempts = 5
////reloadable.cfgs = ["file:/data/collectory/config/Collectory-config.properties"]
//reloadable.cfgs = ["file:/data/${appName}/config/${appName}-config.properties"]
//
///******************************************************************************\
// *  SECURITY
//\******************************************************************************/
//if (!security.cas.uriFilterPattern) {
//    security.cas.uriFilterPattern = "/admin.*,/collection.*,/institution.*,/contact.*,/reports.*," +
//            "/providerCode.*,/providerMap.*,/dataProvider.*,/dataResource.*,/dataHub.*,/manage/.*"
//}
//if (!security.cas.loginUrl) {
//    security.cas.loginUrl = "https://auth.ala.org.au/cas/login"
//}
//if (!security.cas.logoutUrl) {
//    security.cas.logoutUrl = "https://auth.ala.org.au/cas/logout"
//}
//if (!security.apikey.serviceUrl) {
//    security.apikey.serviceUrl = "http://auth.ala.org.au/apikey/ws/check?apikey="
//}
//if(!security.apikey.checkEnabled){
//    security.apikey.checkEnabled = true
//}
//if(!security.cas.appServerName){
//    security.cas.appServerName = "http://localhost:8080"
//}
//if(!security.cas.casServerName){
//    security.cas.casServerName = "https://auth.ala.org.au"
//}
//if(!security.cas.uriExclusionFilterPattern){
//    security.cas.uriExclusionFilterPattern = '/images.*,/css.*,/js.*,/less.*'
//}
//if(!security.cas.authenticateOnlyIfLoggedInPattern){
//    security.cas.authenticateOnlyIfLoggedInPattern = "" // pattern for pages that can optionally display info about the logged-in user
//}
//if(!security.cas.casServerUrlPrefix){
//    security.cas.casServerUrlPrefix = 'https://auth.ala.org.au/cas'
//}
//if(!security.cas.bypass){
//    security.cas.bypass = false
//}
//if(!disableAlertLinks){
//    disableAlertLinks = false
//}
//if(!disableOverviewMap){
//    disableOverviewMap = false
//}
//if(!gbifApiUrl){
//    gbifApiUrl = 'http://api.gbif.org/v0.9'
//}
//
///******************************************************************************\
// *  TEMPLATES
// \******************************************************************************/
//if (!citation.template) {
//    citation.template = 'Records provided by @entityName@, accessed through ALA website.'
//}
//if (!citation.link.template) {
//    citation.link.template = 'For more information: @link@'
//}
//if (!citation.rights.template) {
//    citation.rights.template = ''
//}
//if (!resource.publicArchive.url.template) {
//    resource.publicArchive.url.template = "${biocacheUiURL}/archives/@UID@/@UID@_ror_dwca.zip"
//}
///******************************************************************************\
// *  ADDITIONAL CONFIG
// \******************************************************************************/
//if(!projectNameShort){
//    projectNameShort="Atlas"
//}
//if(!projectName){
//    projectName="Atlas of Living Australia"
//}
//if(!regionName){
//    regionName="Australia"
//}
//if(!collectionsMap.centreMapLon){
//    collectionsMap.centreMapLon = '134'
//}
//if(!collectionsMap.centreMapLat){
//    collectionsMap.centreMapLat = '-28.2'
//}
//if(!collectionsMap.defaultZoom){
//    collectionsMap.defaultZoom = '2'
//}
//if(!eml.organizationName){
//    eml.organizationName="Atlas of Living Australia (ALA)"
//}
//if(!eml.deliveryPoint){
//    eml.deliveryPoint="CSIRO Black Mountain Laboratories, Clunies Ross Street, ACTON"
//}
//if(!eml.city){
//    eml.city="Canberra"
//}
//if(!eml.administrativeArea){
//    eml.administrativeArea="ACT"
//}
//if(!eml.postalCode){
//    eml.postalCode="2601"
//}
//if(!eml.country){
//    eml.country="Australia"
//}
//if(!eml.electronicMailAddress){
//    eml.electronicMailAddress = "info@ala.org.au"
//}
//if(!googleAnalyticsID){
//    googleAnalyticsID = "UA-4355440-1"
//}
//
///******* standard grails **********/
//grails.project.groupId = 'au.org.ala' // change this to alter the default package name and Maven publishing destination
//grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
//grails.mime.use.accept.header = true
//grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
//                      xml: ['text/xml', 'application/xml'],
//                      text: 'text/plain',
//                      js: 'text/javascript',
//                      rss: 'application/rss+xml',
//                      atom: 'application/atom+xml',
//                      css: 'text/css',
//                      csv: 'text/csv',
//                      tsv: 'text/tsv',
//                      all: '*/*',
//                      json: ['application/json','text/json'],
//                      form: 'application/x-www-form-urlencoded',
//                      multipartForm: 'multipart/form-data'
//                    ]
//// URL Mapping Cache Max Size, defaults to 5000
////grails.urlmapping.cache.maxsize = 1000
//
//// The default codec used to encode data with ${}
//grails.views.default.codec="html" // none, html, base64
//grails.views.gsp.encoding="UTF-8"
//grails.converters.encoding="UTF-8"
//// enable Sitemesh preprocessing of GSP pages
//grails.views.gsp.sitemesh.preprocess = true
//// scaffolding templates configuration
//grails.scaffolding.templates.domainSuffix = 'Instance'
//
//// Set to false to use the new Grails 1.2 JSONBuilder in the render method
//grails.json.legacy.builder=false
//// enabled native2ascii conversion of i18n properties files
//grails.enable.native2ascii = true
//// whether to install the java.util.logging bridge for sl4j. Disable fo AppEngine!
//grails.logging.jul.usebridge = true
//// packages to include in Spring bean scanning
//grails.spring.bean.packages = []
//// MEW tell the framework which packages to search for @Validateable classes
//grails.validateable.packages = ['au.org.ala.collectory']
//
///******* location of images **********/
//// default location for images
//repository.location.images = '/data/collectory/data'
//
///******************************************************************************\
// *  ENVIRONMENT SPECIFIC
//\******************************************************************************/
//
//hibernate = "off"
//
///******************************************************************************\
// *  AUDIT LOGGING
//\******************************************************************************/
//auditLog {
//  actorClosure = { request, session ->
//      def cas = session?.getAttribute('_const_cas_assertion_')
//      def actor = cas?.getPrincipal()?.getName()
//      if (!actor) {
//          actor = request.getUserPrincipal()?.attributes?.email
//      }
//      if (!actor) {
//          actor = session.username  // injected by data controller for web services
//      }
//      return actor ?: "anonymous"
//  }
//  TRUNCATE_LENGTH = 2048
//}
//auditLog.verbose = false




//environments {
//    development {
////        grails.serverURL = 'http://dev.ala.org.au:8080/' + appName
////        serverName='http://dev.ala.org.au:8080'
////        security.cas.appServerName = serverName
////        security.cas.contextPath = "/${appName}"
//        grails.resources.debug = true // cache & resources plugins
//    }
//    test {
////        grails.serverURL = 'http://biocache-test.ala.org.au'
////        serverName='http://biocache-test.ala.org.au'
////        security.cas.appServerName = serverName
//        //security.cas.contextPath = "/${appName}"
//    }
//    production {
////        grails.serverURL = 'http://biocache.ala.org.au'
////        serverName='http://biocache.ala.org.au'
////        security.cas.appServerName = serverName
//    }
//}
//
//// log4j configuration
log4j = {
//    // Example of changing the log pattern for the default console appender:
//    //
//    appenders {
//        environments {
//            production {
////                rollingFile name: "tomcatLog", maxFileSize: 102400000, file: "/var/log/tomcat6/${appName}.log", threshold: org.apache.log4j.Level.ERROR, layout: pattern(conversionPattern: "%d %-5p [%c{1}] %m%n")
////                'null' name: "stacktrace"
//                console name: "stdout", layout: pattern(conversionPattern: "%d %-5p [%c{1}]  %m%n"), threshold: org.apache.log4j.Level.WARN
//            }
//            development {
//                console name: "stdout", layout: pattern(conversionPattern: "%d %-5p [%c{1}]  %m%n"), threshold: org.apache.log4j.Level.DEBUG
//            }
//            test {
////                rollingFile name: "tomcatLog", maxFileSize: 102400000, file: "/tmp/${appName}-test.log", threshold: org.apache.log4j.Level.DEBUG, layout: pattern(conversionPattern: "%d %-5p [%c{1}]  %m%n")
////                'null' name: "stacktrace"
//                console name: "stdout", layout: pattern(conversionPattern: "%d %-5p [%c{1}]  %m%n"), threshold: org.apache.log4j.Level.INFO
//            }
//        }
//    }
//
//    root {
//        info 'stdout'
//    }
//
//    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
//            'org.codehaus.groovy.grails.web.pages',          // GSP
//            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
//            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
//            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
//            'org.codehaus.groovy.grails.commons',            // core / classloading
//            'org.codehaus.groovy.grails.plugins',            // plugins
//            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
//            'org.springframework',
//            'org.hibernate',
//            'net.sf.ehcache.hibernate'
//    info   'grails.app'
//    debug  'grails.app.controllers',
//            'grails.app.services',
//            //'grails.app.taglib',
//            'grails.web.pages',
//            //'grails.app',
//            'au.org.ala.cas',
//            'au.org.ala.biocache.hubs',
//            'au.org.ala.biocache.hubs.OccurrenceTagLib'
}
//
//
//
///******************************************************************************\
// *  log4j configuration
//\******************************************************************************/
//logging.dir = (System.getProperty('catalina.base') ? System.getProperty('catalina.base') + '/logs'  : '/var/log/tomcat6')
//log4j = {
//
//    appenders {
//        environments {
//            development {
//                console name: "devLog",
//                        layout: pattern(conversionPattern: "%d %-5p [%c{1}]  %m%n")
//            }
//
//            production {
//                rollingFile name: "prodLog",
//                        maxFileSize: 104857600,
//                        file: logging.dir + "/collectory.log",
//                        layout: pattern(conversionPattern: "%d %-5p [%c{1}]  %m%n")
//                rollingFile name: "stacktrace",
//                        maxFileSize: 104857600,
//                        file: logging.dir + "/collectory-stacktrace.log"
//            }
//        }
//    }
//
//    environments {
//        development {
//            all additivity: false, devLog: [
//                    'grails.app.controllers.au.org.ala.collectory',
//                    'grails.app.domain.au.org.ala.collectory',
//                    'grails.app.services.au.org.ala.collectory',
//                    'grails.app.taglib.au.org.ala.collectory',
//                    'grails.app.conf.au.org.ala.collectory',
//                    'grails.app.filters.au.org.ala.collectory',
//                    'au.org.ala.cas.client'
//            ]
//            all additivity: false, devLog: [
//                    'grails.app.controllers.au.org.ala.collectory',
//                    'grails.app.domain.au.org.ala.collectory',
//                    'grails.app.services.au.org.ala.collectory',
//                    'grails.app.taglib.au.org.ala.collectory',
//                    'grails.app.conf.au.org.ala.collectory',
//                    'grails.app.filters.au.org.ala.collectory',
//                    'au.org.ala.cas.client'
//            ]
//        }
//    }
//
//    root {
//        // change the root logger to my log file
//        error 'devLog', 'prodLog'
//        warn 'devLog', 'prodLog'
//        info 'devLog'
//        debug 'devLog'
//        additivity = true
//    }
//
//    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
//            'org.codehaus.groovy.grails.web.pages', //  GSP
//            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
//            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
//            'org.codehaus.groovy.grails.web.mapping', // URL mapping
//            'org.codehaus.groovy.grails.commons', // core / classloading
//            'org.codehaus.groovy.grails.plugins', // plugins
//            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
//            'org.springframework',
//            'org.hibernate',
//            'net.sf.ehcache.hibernate',
//            'org.codehaus.groovy.grails.plugins.orm.auditable',
//            'org.mortbay.log', 'org.springframework.webflow',
//            'grails.app',
//            'org.apache',
//            'org',
//            'com',
//            'au',
//            'grails.app',
//            'net',
//            'grails.util.GrailsUtil',
//            'grails.app.service.org.grails.plugin.resource',
//            'grails.app.service.org.grails.plugin.resource.ResourceTagLib',
//            'grails.app',
//            'grails.plugin.springcache',
//            'au.org.ala.cas.client',
//            'grails.spring.BeanBuilder',
//            'grails.plugin.webxml',
//            'org.codehaus.groovy.grails.plugins.orm.auditable',
//            'grails-cache-headers',
//            'EhcachePageFragmentCachingFilter',
//            'CacheHeadersGrailsPlugin',
//            'grails.plugin.cache',
//            'grails.plugin.cache.ehcache',
//            'grails.plugin.cache.web.filter.ehcache'
//
//    warn   'org.mortbay.log', 'org.springframework.webflow'
//
//    info   'grails.app.controller'
//
//    debug 'grails.app.controllers.au.org.ala'
//}

grails.cache.config = { }

//log4j.logger.org.springframework.security='off,stdout'
// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
