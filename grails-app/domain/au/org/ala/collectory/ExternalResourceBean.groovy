package au.org.ala.collectory

import au.org.ala.collectory.resources.TaskPhase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * A description of an external resource, intended for collecting a series of resources,
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class ExternalResourceBean implements Comparable<ExternalResourceBean> {
    static mapWith = 'none'
    static Logger LOGGER = LoggerFactory.getLogger(ExternalResourceBean.class)
    static STATUS_LIST = ['local', 'unchanged', 'changed', 'new']
    static STATUS_ORDER = ['local': 3, 'unchanged': 2, 'changed': 1, 'new': 0]

    /** Existing data resource uid */
    String uid
    /** Source dataset name */
    String name
    /** Source dataset identifier */
    String guid
    /** Load status */
    ResourceStatus status
    /** The data source */
    String source
    /** Source updated */
    Date sourceUpdated
    /** Existing Updated */
    Date existingChecked
    /** Add resource */
    Boolean addResource
    /** Update resource metadata */
    Boolean updateMetadata
    /** Update resource data connection */
    Boolean updateConnection
    /** The phase of the resource load */
    TaskPhase phase
    /** Any additional nodes on the resource load */
    List notes = []
    /** The occcurrence data id, used to track data generation tasks */
    String occurrenceId
    /** The number of records in this resource */
    Integer recordCount
    /** The number of records in this resource */
    String country

    /**
     * Add a note to the resource bean load
     *
     * @param note The note to add
     */
    def addNote(String code, Object... args) {
        notes.add([code: code, args: args])
    }

    /**
     * Add an error note to the resource bean load
     * and set the phase to be an error
     *
     * @param note The note to add
     */
    def addError(String code, Object... args) {
        addNote(code, args)
        phase = TaskPhase.ERROR
    }

    /**
     * Check to see if this resource needs an update
     *
     * @return True if there is anything worth updating
     */
    boolean isUpdateRequired() {
        return addResource || updateMetadata || updateConnection
    }

    /**
     * Resolve this against a possible existing resource.
     *
     * @param externalSource The external source name
     *
     */
    DataResource resolve(String externalSource) {
        String euid = uid
        DataResource edr = null
        if (!euid) {
            ExternalIdentifier eid = ExternalIdentifier.findBySourceAndIdentifier(externalSource, guid)
            euid = eid?.entityUid
        }
        if (euid) {
            edr = DataResource.findByUid(euid)
            if (!edr) {
                LOGGER.error("Have has entity uid ${euid} for ${name}/${guid} but no data resource")
            }
        }
        return edr
    }

    /**
     * Order first by status, then by name.
     *
     * @param o The object to compare against
     */
    @Override
    int compareTo(ExternalResourceBean o) {
        int so1 = status.order ?: 0
        int so2 = o.status.order ?: 0
        if (so1 != so2)
            return so1 - so2
        return name.compareTo(o.name) ?: 0
    }

    static enum ResourceStatus {
        UNKNOWN(0),
        NEW(1),
        CHANGED(2),
        UNCHANGED(3),
        LOCAL(4)

        int order

        ResourceStatus(int order) {
            this.order = order
        }
    }
}

