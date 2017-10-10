package au.org.ala.collectory.resources

import au.org.ala.collectory.DataSourceConfiguration
import au.org.ala.collectory.ExternalResourceBean

/**
 * A collection of load tasks, grouped so that the load summary can be displayed on a single page.
 * <p>
 * The load process is
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class DataSourceLoad {
    /** The unique identifier of the data source */
    String guid
    /** The date the load started */
    Date startTime
    /** The date the load finiished */
    Date finishTime
    /** The configuration for the load */
    DataSourceConfiguration configuration

    /**
     * Get the resources to load.
     *
     * @return The resource list from the configuration
     */
    List<ExternalResourceBean> getResources() {
        return configuration.resources
    }

    /**
     * The load is complete when all the resources have reached a terminal phase
     *
     * @return True if all resources have completed
     */
    boolean isComplete() {
        return resources.isEmpty() || resources.every { !it.phase || it.phase.terminal }
    }

    /**
     * Get the percentage of loads coompleted
     *
     * @return The percentage of resources completed
     */
    def getPercentageComplete(){
        //get the number that are complete
        int cs = resources.count { !it.phase || it.phase.terminal }
        int rs = resources.size()
        return rs > 0 ? (cs / rs) * 100 : 100
    }


}
