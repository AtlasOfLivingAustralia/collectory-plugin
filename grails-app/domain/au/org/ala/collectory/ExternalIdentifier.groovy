package au.org.ala.collectory

class ExternalIdentifier {
    String entityUid
    String identifier
    String source
    String uri

    static constraints = {
        entityUid(empty: false)
        identifier(empty: false)
        source(empty: false)
        uri(nullable: true)
    }

    /**
     * Get a label for the identifier
     *
     * @return The combined source and identifier
     */
    String getLabel() {
        return "${source}:${identifier}"
    }

    /**
     * Is this the same identifier?
     *
     * @param o The object to compare against
     *
     * @return True if the source and identifier are equal
     */
    def same(Object o) {
        return (o instanceof ExternalIdentifier) && identifier == o.identifier &&  source == o.source
    }

    def boolean equals(Object o) {
        return (o instanceof ExternalIdentifier) && entityUid == o.entityUid && identifier == o.identifier &&  source == o.source && uri == o.uri
    }
}
