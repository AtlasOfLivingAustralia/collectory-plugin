import org.codehaus.groovy.grails.commons.ConfigurationHolder

dataSource {
	pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "root"
    password = "password"
    logSql = false
}
hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
//environments {
//    development {
//        dataSource {
//            pooled = false
//            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
//            dbCreate = "update" // one of 'create', 'create-drop','update'
//            url = "jdbc:mysql://localhost:3306/collectory?autoReconnect=true&connectTimeout=0"
//            username = "root"
//            password = "password"
//            //logSql = true
//        }
//    }
//	test {
//        dataSource {
//            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
//            dbCreate = "update"
//            url = "jdbc:mysql://localhost:3306/collectory?autoReconnect=true&connectTimeout=0"
//            logSql = false
//            username = "root"
//            password = "password"
//            properties {
//                maxActive = 50
//                maxIdle = 25
//                minIdle = 5
//                initialSize = 5
//                minEvictableIdleTimeMillis = 60000
//                timeBetweenEvictionRunsMillis = 60000
//                maxWait = 10000
//                validationQuery = "/* ping */"
//            }
//        }
//	}
//	production {
//		dataSource {
//            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
//			dbCreate = "update"
//			url = "jdbc:mysql://alaproddb1-cbr.vm.csiro.au:3306/collectory?autoReconnect=true&connectTimeout=0"
//            logSql = false
//            properties {
//                maxActive = 50
//                maxIdle = 25
//                minIdle = 5
//                initialSize = 5
//                minEvictableIdleTimeMillis = 60000
//                timeBetweenEvictionRunsMillis = 60000
//                maxWait = 10000
//                validationQuery = "/* ping */"
//            }
//		}
//	}
//}