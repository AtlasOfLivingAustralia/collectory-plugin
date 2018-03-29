grails.servlet.version = "2.5"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
grails.project.target.level = 1.8
grails.project.source.level = 1.8

//grails.plugin.location."ala-charts-plugin" = "../ala-charts-plugin"

grails.project.fork = [
        test: false,
        run: false
]

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {
    inherits( "global" ) {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        mavenLocal()
        mavenRepo ("https://nexus.ala.org.au/content/groups/public/")
    }

    dependencies {
        //runtime 'mysql:mysql-connector-java:5.1.5' // Needed if you have older versions of mysql installed (5.6)
        runtime 'mysql:mysql-connector-java:5.1.42'
        runtime 'net.sf.opencsv:opencsv:2.3'
	    runtime 'org.apache.ant:ant:1.10.1'
        runtime 'commons-httpclient:commons-httpclient:3.1'
        runtime 'org.aspectj:aspectjweaver:1.6.6'
        compile "com.fasterxml.jackson.core:jackson-databind:2.7.0"
    }

    plugins {
        build(  ":tomcat:7.0.52.1",
                ":release:3.1.2",
                ":rest-client-builder:2.1.1") {
            export = false
        }
        compile ":cache:1.1.8"
        runtime ":hibernate:3.6.10.19"
        runtime ":jquery:1.11.1"
        runtime ":resources:1.2.14"
        runtime ":audit-logging:1.1.3"
        runtime ":cache-headers:1.1.7"
        runtime ":rest:0.8"
        runtime ":tiny-mce:3.4.9"
        runtime ":cors:1.1.8"
        runtime ":ala-charts-plugin:1.3.2"
    }
}
