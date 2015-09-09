grails.servlet.version = "2.5"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.fork = [
        // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
        //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

        // configure settings for the test-app JVM, uses the daemon by default
        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
        // configure settings for the run-app JVM
        run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the run-war JVM
        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
        // configure settings for the Console UI JVM
        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {
    inherits( "global" ) {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {        
        mavenLocal()
        mavenRepo("http://nexus.ala.org.au/content/groups/public/") {
            updatePolicy 'always'
        }
    }

    dependencies {
        runtime 'mysql:mysql-connector-java:5.1.5'
        runtime 'net.sf.opencsv:opencsv:2.3'
	    runtime 'ant:ant:1.6.5'
        runtime 'commons-httpclient:commons-httpclient:3.1'
        runtime 'org.aspectj:aspectjweaver:1.6.6'
    }

    plugins {
        build(  ":tomcat:7.0.52.1",
                ":release:3.0.1",
                ":rest-client-builder:1.0.3") {
            export = false
        }
        compile ":cache:1.1.2"
        runtime ":hibernate:3.6.10.11"
        runtime ":jquery:1.11.1"
        runtime ":resources:1.2.7"
        runtime ":audit-logging:0.5.5.3"
        runtime ":cache-headers:1.1.6"
        runtime ":rest:0.8"
        runtime ":richui:0.8"
        runtime ":tiny-mce:3.4.4"
        runtime ":cors:1.1.8"
    }
}
