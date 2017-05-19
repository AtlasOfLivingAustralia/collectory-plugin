package au.org.ala.collectory.resources

/**
 * The phases of a load or other task from an external resource.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
enum TaskPhase {
    /** New load */
    NEW(false),
    /** Not queued for processing */
    IGNORED(true),
    /** Load queued for processing */
    QUEUED(false),
    /** Load updating metadata */
    METADATA(false),
    /** Generating occurrence data */
    GENERATING(false),
    /** Downloading occurrence data */
    DOWNLOADING(false),
    /** Processing downloaded occurrence data */
    PROCESSING(false),
    /** Connecting to occurrence data */
    CONNECITNG(false),
    /** Load empty */
    EMPTY(true),
    /** Load completed */
    COMPLETED(true),
    /** Load error */
    ERROR(true),
    /** Load cancelled */
    CANCELLED(true)

    boolean terminal

    TaskPhase(boolean terminal) {
        this.terminal = terminal
    }
}
