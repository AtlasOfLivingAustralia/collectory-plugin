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
