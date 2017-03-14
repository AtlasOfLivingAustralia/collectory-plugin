package au.org.ala.collectory

import grails.util.Holders

/**
 * Created by markew
 * Date: Sep 21, 2010
 * Time: 5:06:05 PM
 */
class Utilities {
    public static String buildInstitutionLogoUrl(filename) {
        return Holders.config.grails.serverURL +
                "/data/institution/" + filename
    }
}
