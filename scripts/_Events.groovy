//import org.codehaus.groovy.grails.commons.ConfigurationHolder
//
//eventWebXmlStart = {
////    if (!ConfigurationHolder.config.security.cas.bypass) {
//        def tmpWebXml = "${projectWorkDir}/web.xml.tmp"
//        println "[collectory] projectWorkDir = ${projectWorkDir}"
//        ant.replace(file: tmpWebXml, token: "@security.cas.serverName@", value: ConfigurationHolder.config.security.cas.serverName)
//        println "[collectory] Injecting CAS Security Configuration: serverName = ${ConfigurationHolder.config.security.cas.serverName}"
//        ant.replace(file: tmpWebXml, token: "@security.cas.contextPath@", value: ConfigurationHolder.config.security.cas.contextPath)
//        println "[collectory] Injecting CAS Security Configuration: contextPath = ${ConfigurationHolder.config.security.cas.contextPath}"
//        ant.replace(file: tmpWebXml, token: "/substitute-me", value: ConfigurationHolder.config.security.cas.urlPattern)
//        println "[collectory] Injecting CAS Security Configuration: url pattern = ${ConfigurationHolder.config.security.cas.urlPattern}"
////    }
//}
