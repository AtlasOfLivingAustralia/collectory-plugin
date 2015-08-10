package au.org.ala.collectory

import grails.converters.JSON
import au.org.ala.custom.marshalling.JsonUserType

/**
 * A connection to a source of data.
 * <p>
 * Simple connections are to data maintained by the collectory, including directories of
 * supporting information
 * <p>
 * Complex connections may be to remote sources of data.
 * <p>
 * In both cases, the {@link #connnectionParameters} holds the information about how to connect.
 * When requested, this may be extended by additional information, such as where to find referenced media,
 */
class DataConnection implements Serializable, Cloneable, Comparable<DataConnection> {
    /** The data resource that owns this connection */
    DataResource dataResource
    /** The connection sequence number */
    int sequence = 1
    /** Basic connection parameters */
    Map parameters = [:]
    /** Path to work directory (media, subsitute archives, etc.) */
    String mediaPath
    /** Path to the modified source */
    String modifiedSourcePath
    /** The date the connection was created */
    Date dateCreated = new Date()

    static belongsTo = [dataResource: DataResource]

    static mapping = {

        columns {
            sequence type: 'int'
            parameters type: JsonUserType
        }
    }

    static constraints = {
        mediaPath(nullable: true)
        modifiedSourcePath(nullable: true)
    }

    public DataConnection clone() {
        return (DataConnection) super.clone();
    }

    /**
     * See if this a the same connection data as another connection.
     *
     * @param other Th other connection
     *
     * @return True if the two connection share connection the same parameters
     */
    boolean sameConnection(DataConnection other) {
        return parameters == other?.parameters && mediaPath == other?.mediaPath && modifiedSourcePath == other?.modifiedSourcePath
    }

    /**
     * Data connections are ordered by data resource (if there is one) and then by inverse sequence.
     * <p>
     * This means that the most current data connection is always the first.
     * @param o
     * @return
     */
    @Override
    int compareTo(DataConnection o) {
        if (dataResource == o.dataResource)
            return o.sequence - sequence
        if (dataResource == null && o.dataResource != null)
            return 1;
        if (dataResource != null && o.dataResource == null)
            return -1;
        return dataResource.uid?.compareTo(o.dataResource.uid) ?: 0
    }
}
