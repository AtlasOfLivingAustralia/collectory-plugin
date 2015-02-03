package au.org.ala.collectory

import grails.converters.JSON
import grails.converters.XML
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

/**
 * Request a scan and an update of a data provider that links to a GBIF IPT instance.
 */
class IptController {
    def collectoryAuthService
    def iptService

    /**
     * Scan an IPT instance described by a data provider and provide a list of datasets that need to be updated.
     * Each dataset is mapped onto an ALA data resource; these can be created automatically during the scan.
     * The data provider uid must be provided. There are two additional optional parameters:
     * <dl>
     *    <dt>create (false)</dt>
     *    <dd> If set to true, then any previously unknown datasets have a data resource created and any existing datasets are updates.</dd>
     *    <dt>check (true)</dt>
     *    <dd>If set to true then only report data resources that need updating</dd>
     *    <dt>key (catalogNumber)</dt>
     *    <dd>The term that is used as a key when </dd>
     * <dt>
     * <p>
     * Output formats are JSON, XML or plain text (the default). Plain text is a list of updatable data resource ids
     * suitable for feeding into a shell script.
     */
    def scan() {
        def create = params.create != null && params.create.equalsIgnoreCase("true")
        def check = params.check == null || !params.check.equalsIgnoreCase("false")
        def keyName = params.key ?: 'catalogNumber'
        def provider = ProviderGroup._get(params.uid)
        if (create && !collectoryAuthService.userInRole(ProviderGroup.ROLE_ADMIN)) {
            render (status: 403, text: "Unable to create resources for " + params.uid)
            return
        }
        if (provider == null) {
            render (status: 400, text: "Unable to get data provider " + params.uid)
            return
        }
        def updates = provider == null ? null : iptService.scan(provider, create, check, keyName)
        log.info "${updates.size()} data resources to update for ${params.uid}"
        response.addHeader HttpHeaders.VARY, HttpHeaders.ACCEPT
        withFormat {
            text { render (contentType: 'text/plain', text: updates.findAll({ dr -> dr.uid != null }).collect({ dr -> dr.uid }).join("\n")) }
            xml { render updates as XML }
            json { render updates as JSON }
        }
    }
}