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
    NEW(terminal: false),
    /** Not queued for processing */
    IGNORED(terminal: true),
    /** Load queued for processing */
    QUEUED(terminal: false),
    /** Load updating metadata */
    METADATA(terminal: false),
    /** Generating occurrence data */
    GENERATING(terminal: false),
    /** Downloading occurrence data */
    DOWNLOADING(terminal: false),
    /** Processing downloaded occurrence data */
    PROCESSING(terminal: false),
    /** Connecting to occurrence data */
    CONNECITNG(terminal: false),
    /** Load empty */
    EMPTY(terminal: true),
    /** Load completed */
    COMPLETED(terminal: true),
    /** Load error */
    ERROR(terminal: true),
    /** Load cancelled */
    CANCELLED(terminal: true)

    boolean terminal
}
