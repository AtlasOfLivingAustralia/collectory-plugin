package au.org.ala.collectory

/**
 * Describes relationships between natural history institutions/collections and data providers/resources.
 */
class DataLink {

    String consumer
    String provider

    static constraints = {
        consumer(blank:false)
        provider(blank:false)
    }

}
