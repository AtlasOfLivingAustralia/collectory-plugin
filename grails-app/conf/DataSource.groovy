dataSource {
	pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "root"
    password = ""
    logSql = false
}
hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
}

environments {
    development {
        dataSource {
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://localhost:3306/collectory?autoReconnect=true&connectTimeout=0"
            username = "root"
            password = ""
        }
    }
    test {
        dataSource {
            pooled = true
            dbCreate = 'update'
            driverClassName = "org.h2.Driver"
            url = "jdbc:h2:mem:default"
            username = ""
            password = ""
        }
        /*
        dataSource {
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            dbCreate = "update"
            url = "jdbc:mysql://localhost:3306/collectory?autoReconnect=true&connectTimeout=0"
            logSql = false
            username = "root"
            password = ""
            properties {
                maxActive = 50
                maxIdle = 25
                minIdle = 5
                initialSize = 5
                minEvictableIdleTimeMillis = 60000
                timeBetweenEvictionRunsMillis = 60000
                maxWait = 10000
                validationQuery = ""
            }
        }
        */
    }
    production {
        dataSource {
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            dbCreate = "update"
            url = "jdbc:mysql://localhost:3306/collectory?autoReconnect=true&connectTimeout=0"
            logSql = false
            properties {
                maxActive = 50
                maxIdle = 25
                minIdle = 5
                initialSize = 5
                minEvictableIdleTimeMillis = 60000
                timeBetweenEvictionRunsMillis = 60000
                maxWait = 10000
                validationQuery = ""
            }
        }
    }
}

