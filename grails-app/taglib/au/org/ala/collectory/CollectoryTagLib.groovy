package au.org.ala.collectory

import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder

import java.text.NumberFormat
import java.text.DecimalFormat
import org.codehaus.groovy.grails.web.util.StreamCharBuffer
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONArray
import java.text.SimpleDateFormat
import au.org.ala.collectory.resources.PP

class CollectoryTagLib {

    def authService, metadataService, grailsApplication

    static namespace = 'cl'

    def loggedInName = {
        if (AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE)) {
            out << "logged in as ${AuthenticationCookieUtils.getUserName(request)}"
        } else {
            out << "no cookie found"
        }
    }

    def getFacetForEntity(entity){
        if(entity.ENTITY_TYPE == 'DataResource')
            'data_resource_uid'
        else if (entity.ENTITY_TYPE == 'DataProvider')
            'data_provider_uid'
        else if (entity.ENTITY_TYPE == 'Institution')
            'institution_uid'
        else if (entity.ENTITY_TYPE == 'Collection')
            'collection_uid'
        else if (entity.ENTITY_TYPE == 'DataHub')
            'data_hub_uid'
        else
             ''
    }

    def createNewRecordsAlertsLink = { attrs ->
       createAlertsLink(attrs, '/webservice/createBiocacheNewRecordsAlert')
    }

    def createNewAnnotationsAlertsLink = { attrs ->
       createAlertsLink(attrs, '/webservice/createBiocacheNewAnnotationsAlert')
    }

    def createAlertsLink(attrs, urlPath) {

       if(!grailsApplication.config.disableAlertLinks.toBoolean()){
           def link = grailsApplication.config.alertUrl + urlPath
           link += '?webserviceQuery=/occurrences/search?q=' + attrs.query
           link += '&uiQuery=/occurrences/search?q=' + attrs.query
           link += '&queryDisplayName=' + attrs.displayName
           link += '&baseUrlForWS=' + grailsApplication.config.biocacheServicesUrl
           link += '&baseUrlForUI=' + grailsApplication.config.biocacheUiURL
           link += '&resourceName=' + grailsApplication.config.alertResourceName
           out << "<a href=\"" + link +"\" class='btn' alt='"+attrs.altText+"'><i class='icon icon-bell'></i> "+ attrs.linkText + "</a>"
       }
    }

    /**
     * Generate the link the login link for the banner.
     *
     * Will be to log in or out based on current auth status.
     */
    def loginoutLink = {
        def requestUri = grailsApplication.config.security.cas.serverName + request.forwardURI
        if (AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE)) {
            // currently logged in
            out << "<li class='nav-logout nav-right'><a id='${AuthenticationCookieUtils.getUserName(request)}' href='https://auth.ala.org.au/cas/logout?url=${requestUri}'><span>Log out</span></a></li>"
        } else {
            // currently logged out
            out << "<li class='nav-login nav-right'><a href='https://auth.ala.org.au/cas/login?service=${requestUri}'><span>Log in</span></a></li>"
        }
    }

    /**
     * Generate the login link for the banner.
     *
     * Will be to log in or out based on current auth status.
     * TODO: also check for user principal in request in case cookies are disabled
     * 
     * @attr showUser if supplied, the username will be shown before the logout link
     * @attr fixedAppUrl if supplied will be used for logout instead of the current page
     */
    def loginoutLink2011 = { attrs ->
        def requestUri = grailsApplication.config.security.cas.serverName + request.forwardURI
        if (AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE)) {
            // currently logged in
            if (attrs.showUser) {
                out << "<span id='logged-in'>Logged in as ${loggedInUsername()}</span>"
            }
            out << link(controller: 'public', action: 'logout',
                    params: [casUrl: grailsApplication.config.security.cas.logoutUrl,
                    appUrl: attrs.fixedAppUrl ?: requestUri]) {'Logout'}
        } else {
            // currently logged out
            out << "<a href='https://auth.ala.org.au/cas/login?service=${requestUri}'><span>Log in</span></a>"
        }
    }

    /**
     * Decorates the role if present
     *
     * @attrs role the role to display
     */
    def roleIfPresent = { attrs, body ->
        out << (!attrs.role ? '' : ' - ' + attrs.role.encodeAsHTML())
    }

    /**
     * Indicates user can edit if admin
     *
     * @attrs admin - is the user an admin for the collection
     */
    def adminIfPresent = { attrs, body ->
        out << (attrs.admin ? '(Authorised to edit this collection)' : '')
    }

    /**
     * <g:ifAllGranted role="ROLE_COLLECTION_EDITOR,ROLE_COLLECTION_ADMIN">
     *  All the listed roles must be granted for the tag to output its body.
     * </g:ifAllGranted>
     */
    def ifAllGranted = { attrs, body ->
        def granted = true
        if (grailsApplication.config.security.cas.bypass) {
            granted = true
        } else {
            def roles = attrs.role.toString().tokenize(',')
            roles.each {
                if (!request.isUserInRole(it)) {
                    granted = false
                }
            }
        }
        if (granted) {
            out << body()
        }
    }

    /**
     * <cl:ifGranted role="ROLE_COLLECTION_ADMIN">
     *  The specified role must be granted for the tag to output its body.
     * </g:ifGranted>
     * @attr role the role to check
     */
    def ifGranted = { attrs, body ->
        if (grailsApplication.config.security.cas.bypass || request.isUserInRole(attrs.role)) {
            out << body()
        }
    }

    /**
     * <cl:ifNotGranted role="ROLE_COLLECTION_ADMIN">
     *  The specified role must be missing for the tag to output its body.
     * </g:ifNotGranted>
     * @attr role the role to check
     */
    def ifNotGranted = { attrs, body ->
        if (!grailsApplication.config.security.cas.bypass && !request.isUserInRole(attrs.role)) {
            out << body()
        }
    }

    /**
     * List interesting roles.
     */
    def roles = {
        def roles = []
        ['ROLE_ADMIN','ROLE_COLLECTION_EDITOR','ROLE_COLLECTION_ADMIN'].each {
            if (authService.userInRole(it)) {
                roles << it
            }
        }
        out << roles.join(', ')
    }

    def isLoggedIn = { attrs, body ->
        if (AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE)) {
            out << body()
        }
    }

    def isNotLoggedIn = {attrs, body ->
        if (!grailsApplication.config.security.cas.bypass && !AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE)) {
            out << body()
        }
    }

    def loggedInUsername = {
        if (grailsApplication.config.security.cas.bypass) {
            out << 'cas bypassed'
        }
        else if (request.getUserPrincipal()) {
        	out << request.getUserPrincipal().name
        }
        else if (AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE)) {
            out << AuthenticationCookieUtils.getUserName(request) + '*'
        }
    }

    private boolean isAdmin() {
        return grailsApplication.config.security.cas.bypass || request?.isUserInRole(ProviderGroup.ROLE_ADMIN)
    }

    /**
     * Determines whether the person with the specified email has the rights to edit the specified entity.
     * A person can edit an entity if:
     * 1) they are admin,
     * 2) they are a contact for the entity with administrator privilege,
     * 3) they are a contact for a parent of the entity with administrator privilege.
     *
     * @param uid the uid of the entity
     * @param email the email (username) of the person to check
     * @return true if has rights to edit
     */
    private boolean isAuthorisedToEdit(uid, email) {
        if (isAdmin()) {
            return true
        }
        else if (email) {
            ProviderGroup pg = ProviderGroup._get(uid)
            if (pg) {
                return pg.isAuthorised(email)
            }
        }
        return false
    }

    /**
     * Authorisation for editing is determined by roles and rights
     *
     * @attrs uid - the uid of the entity
     */
    def isAuth = { attrs, body ->
        if (isAuthorisedToEdit(attrs.uid, request.getUserPrincipal()?.attributes?.email)) {
            out << body()
        } else {
            out << ' You are not authorised to change this record '// + debugString
        }
    }

    /**
     * Writes the main heading of the page - usually the entity name.
     * Font size is adjusted for longer text.
     *
     * @attr value the text to display
     * @attr edit the id for an optional edit link
     * @attr id optional id for the h1 element
     */
    def h1 = { attrs ->
        def style = ""
        if (attrs.value?.size() > 70) {
            style = ' style="font-size:1.6em;"'
        } else
        if (attrs.value?.size() > 58) {
            style = ' style="font-size:1.7em;"'
        } else
        if (attrs.value?.size() > 50) {
            style = ' style="font-size:1.8em;"'
        }
        def editLink = ""
        if (attrs.edit) {
            editLink = change(id: attrs.edit)
        }
        def id = attrs.id ? " id=${attrs.id}" : ""

        out << "<h1${style}${id}>${attrs.value}${editLink}</h1>"
    }

    def change = { attrs ->
        out << "<img id='${attrs.id}' class='changeLink' src='${resource(dir:'images/admin',file:'change.png')}'/>"
    }

    def showDecimal = { attrs ->
        BigDecimal val = -1
        if (attrs.value?.class == BigDecimal.class) {
            val = attrs.value
        } else if (attrs.value?.class == StreamCharBuffer.class) {
            try {
                val = new BigDecimal(Double.parseDouble(attrs.value.toString()))
            } catch (NumberFormatException e) {}
        }
        if (val != -1) {
            NumberFormat formatter = new DecimalFormat("#0.000000")
            out << formatter.format(val)
            if (attrs.degree) {
                out << '&deg;'
            }
        }
    }

    def showNumber = { attrs ->
        if (attrs.value && attrs.value != -1) {
            out << attrs.value
        }
    }

    /**
     * Show number and body if it is not -1 (indicated info not available)
     *
     * @attrs number
     */
    def numberIfKnown = {attrs, body ->
        out << (attrs.number == -1 ? "" : attrs.number + body())
    }

    /**
     * Outputs value with some decoration if value is not blank.
     * Handles up to 3 values but stops when any value is blank.
     *
     * TODO: this is a pretty shit tag - should be refactored
     * 
     * @attrs value the value to test and output
     * @attrs tagName the tag to enclose the value in - defaults to p if not specified
     * @attrs prefix output before value
     * @attrs postfix output after value
     * @attrs join separator between multiple values
     */
    def ifNotBlank = {attrs ->
        def tagName = (attrs.tagName == null) ? 'p' : attrs.tagName
        def startTag = tagName ? "<${tagName}>" : ""
        def endTag = tagName ? "</${tagName}>" : ""
        if (attrs.value) {
            out << startTag
            out << (attrs.prefix) ? attrs.prefix : ""
            out << attrs.value.encodeAsHTML()
            out << (attrs.postfix) ? attrs.postfix : ""

            // allow other content
            if (attrs.value2) {
                out << (attrs.join) ? attrs.join : ""
                out << (attrs.prefix) ? attrs.prefix : ""
                out << attrs.value2
                out << (attrs.postfix) ? attrs.postfix : ""

                if (attrs.value3) {
                    out << (attrs.join) ? attrs.join : ""
                    out << (attrs.prefix) ? attrs.prefix : ""
                    out << attrs.value3
                    out << (attrs.postfix) ? attrs.postfix : ""
                }
            }

            out << endTag
        }
    }

    /**
     * Outputs value/body if one is not blank otherwise outputs the otherwise value if present.
     * Can be used as:
     *  <valueOrOtherwise>bod</> => outputs bod if bod is groovy true
     *  <valueOrOtherwise value='val'></> => outputs val if val is groovy true
     *  <valueOrOtherwise value='val'>bod</> => outputs bod if val is groovy true
     * In any of the above:
     *  <valueOrOtherwise value='val' otherwise='not defined'>bod</> => outputs not defined if nothing is groovy true
     *
     * @attrs value the value to test (and output if true and there is no body)
     * @attrs otherwise the text to output if tests all fail
     * @body the value to test (if value is not provided) and output if test is true
     */
    def valueOrOtherwise = { attrs, body ->
        // determine what text to show
        def value = attrs.value
        def bod = body()
        def text = ''
        if (body() && body() != "") {
            text = body()
        } else if (attrs.value) {
            text = attrs.value
        }
        // determine whether to show it
        if (attrs.value) {
            out << text
        } else if (!attrs.containsKey('value') && body()) { // only test body if value was not present (cf was null, blank or false)
            out << text
        } else if (attrs.otherwise) {
            out << attrs.otherwise
        }
    }

    def acronymOrShortName = { attrs ->
        def pg = attrs.entity
        if (pg) {
            if (pg.acronym) {
                out << pg.acronym
            } else {
                out << pg.name
            }
        }
    }

    def formatPercent = { attrs ->
        double percent
        try {
            percent = attrs.percent as double
        } catch (Exception e) {
            //out << e.message
            return
        }
        NumberFormat formatter
        if (percent == 0.0) {
            formatter = new DecimalFormat("#0")
        } else if (percent < 0.1) {
            formatter = new DecimalFormat("#0.00")
        } else if (percent > 50.0) {
            formatter = new DecimalFormat("#0")
        } else {
            formatter = new DecimalFormat("#0.0")
        }
        out << formatter.format(percent)
    }

    def percentIfKnown = { attrs ->
        double dividend
        double divisor
        try {
            dividend = attrs.dividend as double
            divisor = attrs.divisor as double
        } catch (Exception e) {
            //out << e.message
            return
        }
        //out << "dividend=" << dividend << " "
        //out << "divisor=" << divisor
        if (attrs.dividend == -1 || attrs.divisor == -1) {
            return
        }
        if (dividend && divisor) {
            double percent = dividend / divisor * 100
            NumberFormat formatter = new DecimalFormat("#0.0")
            out << formatter.format(percent) << ' %'
        }
    }

    def ifLSID = {attrs, body ->
        out << (body().startsWith('urn:lsid:') ? body() : "")
    }

    /**
     * Show lsid as a link
     *
     * @attrs guid only shown if it is an lsid
     * @attrs target for the link
     */
    def guid = { attrs ->
        def lsid = attrs.guid
        def target = attrs.target
        if (target) {
            target = " target='" + target + "'"
        } else {
            target = ""
        }
        if (lsid =~ 'lsid') {  // contains
            def authority = lsid[9..lsid.indexOf(':',10)-1]
            out << "<a${target} rel='nofollow' class='external' href='http://${authority}/${lsid.encodeAsHTML()}'>${lsid.encodeAsHTML()}</a>"
        } else {
            out << attrs.guid
        }
    }

    /** Checkbox list that can be used as a more user-friendly alternative to
     *  a multiselect list box
     *
     * @attrs from the list of all values
     * @attrs value the list of selected values
     * @attrs name the name of the checkbox list
     * @attrs height optional height
     * @attrs width optional width
     * @attrs optionKey optional optionKey to use in list
     * @attrs readonly creates list for read only
     */
    def checkBoxList = {attrs, body ->
        def from = attrs.from
        def value = attrs.value
        def cname = attrs.name
        def isChecked, ht, wd, style, html

        //  sets the style to override height and/or width if either of them
        //  is specified, else the default from the CSS is taken
        style = "style='"
        if(attrs.height)
            style += "height:${attrs.height};"
        if(attrs.width)
            style += "width:${attrs.width};"

        if (style.length() == "style='".length())
            style = ""
        else
            style += "'" // closing single quote

        html = "<ul class='CheckBoxList' " + style + ">"

        out << html

        from.each { obj ->

            if (attrs.optionKey) {
                isChecked = (value?.contains(obj."${attrs.optionKey}"))? true: false
                out << "<li>" <<
                    checkBox(name:cname, value:obj."${attrs.optionKey}", checked: isChecked, disabled: attrs.readonly) <<
                        "${obj}" << "</li>"
            } else {
                isChecked = (value?.contains(obj))? true: false
                out << "<li>" <<
                    checkBox(name:cname, value:obj, checked: isChecked, disabled: attrs.readonly) <<
                        "${obj}" << "</li>"
            }

        }

        out << "</ul>"

    }

    /**
     * Converts a ProviderGroup groupType to a controller name
     */
    def controller = {attrs ->
        switch (attrs.type) {
            case Collection.ENTITY_TYPE: out << 'collection'; return
            case Institution.ENTITY_TYPE: out << 'institution'; return
            case DataProvider.ENTITY_TYPE: out << 'dataProvider'; return
            case DataResource.ENTITY_TYPE: out << 'dataResource'; return
            case DataHub.ENTITY_TYPE: out << 'dataHub'; return
        }
    }

    /**
     * Inserts a help button in a <td> element which will toggle the visibility of a help div in the previous <td> element.
     */
    def helpTD = {
        //out << '<td><img class="helpButton" alt="help" src="' + resource(dir:'images/skin', file:'help.gif') + '" onclick="toggleHelp(this);"/></td>'
    }

    /**
     * Inserts a help button which will toggle the visibility of a help div in the previous <td> element.
     */
    def help = { attrs ->
        if (attrs.javascript) {
            out << "<img style='float:right;margin-right:20px;' class='helpButton' alt='help' src='" +
                    resource(dir:"images/skin", file:"help.gif") +
                    "' onclick='${attrs.javascript}'/>"
        } else {
            out << '<img style="float:right;margin-right:20px;" class="helpButton" alt="help" src="' +
                    resource(dir:'images/skin', file:'help.gif') +
                    '" onclick="toggleHelp(this);"/>'
        }
    }

    /**
     * Displays a JSON list as a comma-separated list of strings.
     * The last separator is the word 'and', unless pureList is true.
     *
     * @attr json the json array
     * #attr pureList if true only commas are use as separators and no spaces are injected
     */
    def JSONListAsStrings = {attrs ->
        if (!attrs?.json) { return "" }
        def str = ""
        def list = JSON.parse(attrs.json.toString())
        switch (list.size()) {
            case 0: break;
            case 1: str = list[0]; break;
            default:
                if (attrs.pureList == 'true') {
                    str = list[0..-1].sort().join(', ')  // bizarrely this gives a different output to list.join(', ')
                    //str = list.join(', ')
                }
                else {
                    str = list[0..-2].join(', ') + " and " + list.last()
                }
                break
            }
        out << str
    }

    /**
     * Displays a space-separated list of strings as a comma-separated list of strings.
     * The last separator is the word 'and'.
     */
    def concatenateStrings = {attrs ->
        if (!attrs.values)
            return ""
        def list = attrs.values.tokenize(' ')
        out << (list.size() == 1 ? list[0] : list[0..list.size()-2].join(', ') + " and " + list.last())
    }

    /**
     * Displays a JSON list as an unordered list of strings
     */
    def JSONListAsList = {attrs ->
        if (!attrs?.json)
            return ""
        def list = JSON.parse(attrs.json.toString())
        out << "<ul>"
        list.each {out << "<li>${it.encodeAsHTML()}</li>"}
        out << "</ul>"
    }

    def membershipWithGraphics = { attrs ->
        ProviderGroup pg = attrs.coll
        if (pg) {
            // check collection's membership
            ProviderGroup.networkTypes.each {
                if (pg.isMemberOf(it)) {
                    out << "<span class='label'>Member of ${it} </span> <br/>"
                    // this will be tidied up when hubs are entities
                    if (it == "CHAH") {
                        out << "<img class='img-polaroid' style='padding-left:25px;' src='" + resource(absolute:"true", dir:"data/network/",file:"CHAH_logo_col_70px_white.gif") + "'/>"
                    }
                    if (it == "CHAEC") {
                        out << "<img class='img-polaroid' src='" + resource(absolute:"true", dir:"data/network/",file:"chaec-logo.png") + "'/>"
                    }
                    if (it == "CHAFC") {
                        out << "<img class='img-polaroid' src='" + resource(absolute:"true", dir:"data/network/",file:"chafc.png") + "'/>"
                    }
                    if (it == "CHACM") {
                        out << "<img class='img-polaroid' src='" + resource(absolute:"true", dir:"data/network/",file:"chacm.png") + "'/>"
                    }
                    out << "<br/>"
                }
            }
            // check institution membership
            /*if (coll.institution) {
                ProviderGroup.networkTypes.each {
                    if (coll.institution.isMemberOf(it)) {
                        out << it
                    }
                }
            }*/
        }
    }

    /**
     * Displays label, count and percent of total as 3 table columns
     * @param total - divisor in percent calculation
     * @param with - count to display, dividend in percent calc
     * @param without - 'inverse' of count to display, ie display total - this number
     */
    def totalAndPercent = {attrs ->
        def count = 0
        if (attrs.with) {
            count = attrs.with
        } else if (attrs.without) {
            count = attrs.total - attrs.without
        }
        out << """<td>${attrs.label}</td><td>${count}</td>\n
         <td>${cl.percentIfKnown(dividend:count, divisor:attrs.total)}</td>"""
    }

//    /**
//     * Inserts a hidden div holding the specified help text.
//     */
//    def helpText = { attrs ->
//        def _default = attrs.default ? attrs.default : ""
//        def id = attrs.id ? "id='${attrs.id}' " : ""
//        out << '<div ' + id + 'class="fieldHelp" style="display:none">' + message(code:attrs.code, default: _default) + '</div>'
//    }

    /**
     * @attr title
     * @attr printable Is this being printed?
     * body content should contain the help text
     */
    def helpText = { attrs, body ->
        def _default = attrs.default ? attrs.default : ""
        def helpTitle = message(code:attrs.code+".title", default: '')
        def mb = new MarkupBuilder(out)
        mb.a(href:'#', class:'helphover', 'data-original-title':helpTitle, 'data-content':message(code:attrs.code, default: _default)) {
            i(class:'icon-question-sign') {
                mkp.yieldUnescaped("&nbsp;")
            }
        }
    }

    /**
     * Selects the words to describe the start and end dates of a collection based on data availability.
     * @attr start the start date
     * @attr end the end date
     * @attr change if true show a placeholder instead of nothing when there is no data
     */
    def temporalSpanText = { attrs ->
        if (attrs.start && attrs.end) {
            out << "The collection was established in ${attrs.start} and ceased acquisitions in ${attrs.end}."
        } else if (attrs.start) {
            out << "The collection was established in ${attrs.start} and continues to the present."
        } else if (attrs.end) {
            out << "The collection ceased acquisitions in ${attrs.end}."
        } else if (attrs.change) {
            out << "[No start or end date specified for this collection.]"
        }
    }

    def stateCoverage = {attrs ->
        if (!attrs.states) return
        if (attrs.states.toLowerCase() in ["all", "all states", "australian states"]) {
            out << "All Australian states are covered."
        } else {
            out << "Australian states covered include: " + attrs.states.encodeAsHTML() + "."
        }
    }

    /**
     * A little bit of email scrambling for dumb scrappers.
     *
     * Uses email attribute as email if present else uses the body.
     * If no attribute and the body is not an email address then nothing is shown.
     *
     * @attrs email the address to decorate
     * @body the text to use as the link text
     */
    def emailLink = { attrs, body ->
        def strEncodedAtSign = "(SPAM_MAIL@ALA.ORG.AU)"
        String email = attrs.email
        if (!email)
            email = body().toString()
        int index = email.indexOf('@')
        if (index > 0) {
            email = email.replaceAll("@", strEncodedAtSign)
            out << "<span class='link under' onclick=\"return sendEmail('${email}')\">${body()}</span>"
        }
    }

    def emailBugLink = { attrs, body ->
        def strEncodedAtSign = "(SPAM_MAIL@ALA.ORG.AU)"
        String email = attrs.email
        if (!email)
            email = body().toString()
        int index = email.indexOf('@')
        //println "index=${index}"
        if (index > 0) {
            email = email.replaceAll("@", strEncodedAtSign)
        }
        out << "<span class='link' onclick=\"return sendBugEmail('${email}','${attrs.message}')\">${body()}</span>"
    }

    /**
     * Massages the collection name for display.
     *  - adds collection at the end unless the name contains collection or herbarium
     *  - adds the specified prefix if the name doesn't already start with it (handles collections that start with 'The')
     *
     * @name name of the collection
     * @prefix added before the name
     */
    def collectionName = { attrs ->
        def name = attrs.name
        if (name && !(name =~ 'Collection' || name =~ 'Herbarium')) {
            name += " collection"
        }
        if (attrs.prefix && !name.toLowerCase().startsWith(attrs.prefix.toLowerCase())) {
            name = attrs.prefix + name
        }
        out << name
    }

    def subCollectionDisplay = { attrs ->
        if (attrs.sub?.description) {
            out << "<strong>" + attrs.sub?.name + "</strong>" + " - " + attrs.sub?.description
        } else {
            out << "<strong>" + attrs.sub?.name + "</strong>"
        }
    }

    def subCollectionList = { attrs ->
        if (attrs.list) {
            try {
                out << "<ul class='fancy'>"
                JSON.parse(attrs.list).each { sub ->
                    out << "<li>${cl.subCollectionDisplay(sub: sub)}</li>"
                }
                out << "</ul>"
            } catch (ConverterException e) {
                out  << "unable to parse sub-collections"
            }
        }
    }

    /**
     * Takes the values as java list or JSON string and sets up checkboxes.
     */
    def checkboxSelect = {attrs ->
        out << "<table class='span8'><tr>"
        //log.info "attrs.value=${attrs.value}"
        attrs.from.eachWithIndex { it, index ->
            def checked
            if (attrs.value instanceof String) {
                checked = (attrs.value.indexOf(it) >= 0) ? "checked='checked'" : ""
            } else {
                checked = (it in attrs.value) ? "checked='checked'" : ""
            }
            out << "<td><input name='${attrs.name}' type='checkbox' ${checked}' value='${it}'/>${it}</td>"
            if (index > 0 && ((index+1) % 6) == 0) {
                out << "</tr>"
            }
        }
        out << "</tr></table>"
    }

    def raw = { attrs->
        if (!attrs.value) {
            out << attrs.default
        }
        else {
            out << attrs.value
        }
    }

    def formatJsonList = { attrs ->
       if(attrs.value){
           try {
               def js = new JsonSlurper()
               def list = js.parseText(attrs.value?.toString())
               out << list.join(", ")
           } catch (Exception e){
               out << attrs.value
           }
       }
    }

    /**
     * Formats free text so:
     *  line feeds are honoured
     *  and urls are linked
     *  and bold (+xyz+)and italic (_xyz_) are rendered
     *  and lists are supported using wiki markup
     *
     * @param attrs.noLink suppresses links
     * @param attrs.noList suppresses lists
     * @param body the text to format
     * @param pClass the class to use for paras
     * @param body pass the text in as a attr rather than as body (won't be encoded)
     */
    def formattedText = {attrs, body ->
        def text = attrs.body ?: body().toString()
        if (text) {

            if (text.indexOf('<') >= 0 && text.indexOf('>') >= 0) {
                // assume this is already marked up as html
                out << text
            }
            else {

                // italic
                def italicMarkup = /(\b)_([^\r\n_]*)_(\b)/  // word boundary _ thing to be italised _ word boundary
                text = text.replaceAll(italicMarkup) {match, s1, s2, s3 ->
                    s1 + '<em>' + s2 + '</em>' + s3         // word boundary <em> thing to be italised </em> word boundary
                }

                // in-line links
                if (!attrs.noLink) {
                    def urlMatch = /[^\[](http:\S*)\b/   // word boundary + http: + non-whitespace + word boundary
                    text = text.replaceAll(urlMatch) {s1, s2 ->
                        if (s2.indexOf('ala.org.au') > 0)
                            " <a href='${s2}'>${s2}</a>"
                        else
                            " <a rel='nofollow' class='external' target='_blank' href='${s2}'>${s2}</a>"
                    }
                }

                // wiki-like links
                if (!attrs.noLink) {
                    def urlMatch = /\[(http:\S*)\b ([^\]]*)\]/   // [http: + text to next word boundary + space + all text ubtil next ]
                    text = text.replaceAll(urlMatch) {s1, s2, s3 ->
                        if (s2.indexOf('ala.org.au') > 0)
                            "<a href='${s2}'>${s3}</a>"
                        else
                            "<a rel='nofollow' class='external' target='_blank' href='${s2}'>${s3}</a>"
                    }
                }

                // bold
                def regex = /\+([^\r\n+]*)\+/
                text = text.replaceAll(regex) {match, group -> '<b>' + group + '</b>'}

                // lists
                if (!attrs.noList) {
                    def lines = text.tokenize("\r\n")
                    def inList = false
                    def newText = ""
                    // for each line
                    lines.each {
                        if (it[0] == '*') {
                            // replace list markup
                            def item = "<li>" + it.substring(1,it.length()) + "</li>"
                            if (inList) {
                                it = item
                            } else {
                                inList = true
                                it = "<ul class='simple'>" + item
                            }
                        } else {
                            if (it) { // skip blank content
                                def para = (attrs.pClass) ? "<p class='${attrs.pClass}'>" : "<p>"
                                it = para + it + "</p>"
                            }
                            if (inList) {
                                inList = false
                                it = "</ul>" + it
                            }
                        }
                        newText += it
                    }
                    if (inList) { newText = newText + "</ul>"}
                    text = newText
                }

                out << text
            }
        }
    }

    def textAreaHeight = {attrs ->
        def text = attrs.text
        if (text) {
            int lines = text.length()/90
            lines += text.count('\n')
            //int charCount = s.length() - s.replaceAll("\\.", "").length();
            lines = Math.min(lines, 20)
            lines = Math.max(lines, 4)
            out << lines
        } else {
            out << 4
        }
    }

    /**
     * Builds the link to bio-cache records for the passed entity.
     *
     * @param entity the entity to search for
     * @param collection (same as entity - for backwards compat)
     * @param onlyIf a boolean switch
     * @body the body of the link
     */
    def recordsLink = {attrs, body ->
        def pg = attrs.entity ? attrs.entity : attrs.collection
        if (pg) {
            if (attrs.containsKey('onlyIf') && !attrs.onlyIf) {
                out << body()
            }
            else {
                out << "<a class='recordsLink' href='"
                out << buildRecordsUrl(pg.uid)
                out << "'>" << body() << "</a>"
            }
        }
    }

    /**
     * Builds a link to the publicly available archive of all records.
     *
     * @attr uid of the instance (assumed to be a data resource)
     * @attr allowed if the archive is available
     */
    def downloadRecordsLink = {attrs, body ->
        if (attrs.uid && attrs.allowed) {
            def url = grailsApplication.config.biocache.baseURL +
                    "/occurrences/download?q=*.*&fq=collection_uid=${attrs.uid}#download"
            out << "<a href='${url}'>Download all records</a>"
        }
    }

    def downloadPublicArchive = { attrs ->
        if (attrs.uid && attrs.available) {
            def url = grailsApplication.config.resource.publicArchive.url.template.replaceAll('@UID@',attrs.uid)
            out << "<p><a class='downloadLink' href='${url}'>Download darwin core archive of all records</a></p>"
        }
    }

    def publicArchiveLink = { attrs ->
        if (attrs.uid && attrs.available) {
            def url = grailsApplication.config.resource.publicArchive.url.template.replaceAll('@UID@',attrs.uid)
            out << "<a href='${url}'>${url}</a>"
        }
    }

    private String buildRecordsUrl(uid) {
        // handle descendant institutions
        def uidStr = uid;
        if (uid.size() > 1 && uid[0..1] == 'in') {
            uidStr = Institution._get(uid)?.descendantUids()?.join(",") ?: uid
        }
        def queryStr
        // need to handle multiple uids differently
        if (uidStr.indexOf(',')) {
            def uids = uidStr.tokenize(',')
            queryStr = uids.collect({ fieldNameForSearch(it) + ":" + it}).join(' OR ')
        }
        else {
            queryStr = fieldNameForSearch(uidStr) + ":" + uidStr
        }
        return  grailsApplication.config.biocacheUiURL + "/occurrence/search?q=" + queryStr
    }

    private String fieldNameForSearch(uid) {
        switch (uid[0..1]) {
            case 'co': return 'collection_uid'; break
            case 'in': return 'institution_uid'; break
            case 'dr': return 'data_resource_uid'; break
            case 'dp': return 'data_provider_uid'; break
            case 'dh': return 'data_hub_uid'; break
            default: return ""
        }
    }

    def warnIfInexactMapping = { attrs ->
        if (attrs.collection?.isInexactlyMapped()) {
            out << "<div id='warnings'><h4>Warning</h4>Records do not exactly match this collection."
            out << "<br/>${attrs.collection?.providerMap?.warning}</div>"
        }
    }

    def numberOf = {attrs->
        if (attrs.number) {
            NumberFormat formatter = new DecimalFormat(",###")
            out << formatter.format(attrs.number)
        } else {
            out << attrs.none ?: "no"
        }
        out << " "
        out << (attrs.number == 1 ? attrs.noun : attrs.noun + "s")
    }

    def permalink = {attrs, body ->
        def context
        switch (attrs.type) {
            case 'institution': context = '/public/showInstitution/'; break
            case 'collection': context = '/public/show/'; break
            default: context = '/public/show/'; break
        }
        if (context) {
            def url = grailsApplication.config.grails.serverURL + context + attrs.id
            out << "<a href='${url}'>"
            out << ((body() != "") ? body() : context + attrs.id)
            out << "</a>"
        }
    }

    /**
     * Show map of records based on UID
     *
     * - content is loaded by ajax calls
     */
    def recordsMap = {
        out <<
            "<div class='recordsMap'>" +
            " <img id='recordsMap' class='no-radius' src='${resource(dir:'images/map',file:'map-loader.gif')}' width='340' />" +
            " <img id='mapLegend' src='${resource(dir:'images/ala', file:'legend-not-available.png')}' width='128' />" +
            "</div>" +
            "<div class='learnMaps'><span class='asterisk-container'><a href='${ConfigurationHolder.config.ala.baseURL}/about/progress/map-ranges/'>Learn more about Atlas maps</a>&nbsp;</span></div>"
    }

    /**
     * Show map of records based on UID
     *
     * - content is loaded directly by constructing the image url
     * 
     * @attr uid of the instance
     */
    def recordsMapDirect = { attrs ->
        if (attrs.uid) {
            def urlBase = grailsApplication.config.biocacheServicesUrl + "/density/"
            def query = "?q=" + fieldNameForSearch(attrs.uid) + ":" + attrs.uid
            out <<
                "<div class='recordsMap'>" +
                " <img id='recordsMap' class='no-radius' src='${urlBase}map${query}' width='340' />" +
                " <img id='mapLegend' src='${urlBase}legend${query}' width='128' />" +
                "</div>" +
                "<div class='learnMaps'><span class='asterisk-container'><a href='${ConfigurationHolder.config.ala.baseURL}/faq/species-data/errors-in-maps/'>Learn more about Atlas maps</a>&nbsp;</span></div>"
        }
        else {
            out <<
                "<div class='recordsMap'>" +
                " <img id='recordsMap' class='no-radius' src='${resource(dir:'images/map',file:'mapping-data-not-available.png')}' width='340' />" +
                " <img id='mapLegend' src='${resource(dir:'images/ala', file:'legend-not-available.png')}' width='128' />" +
                "</div>" +
                "<div class='learnMaps'><span class='asterisk-container'><a href='${ConfigurationHolder.config.ala.baseURL}/faq/species-data/errors-in-maps/'>Learn more about Atlas maps</a>&nbsp;</span></div>"
        }
    }

    /**
     * Show map of records based on UID
     *
     * - different structure to above - to accommodate 2 column layout
     * TODO: this should be rationalised
     * @deprecated use recordsMapDirect
     */
    def collectionRecordsMap = {
        out <<
            "<div class='collectionRecordsMap'>" +
            " <img id='recordsMap' class='no-radius' src='${resource(dir:'images/map',file:'map-loader.gif')}' width='340' />" +
            " <img id='mapLegend' src='${resource(dir:'images/ala', file:'legend-not-available.png')}' width='128' />" +
            " <div class='learnMaps'><span class='asterisk-container'><a href='${ConfigurationHolder.config.ala.baseURL}/about/progress/map-ranges/'>Learn more about Atlas maps</a>&nbsp;</span></div>" +
            "</div>"
    }

    /**
     * @deprecated
     */
    def decadeBreakdown = {attrs ->
        if (attrs.data) {
            def labels = ""
            def values = ""
            out << "<ul>"
            attrs.data.toArray().each {
                labels += "|" + it.label
                values += it.count + ","
                out << "<li>${it.label}: ${it.count}</li>"
            }
            values = "1,16,44,22,66,19,2"
            out << "</ul>"
            out << "<img src='http://chart.apis.google.com/chart?chxl=1:|1870|1880|1890|1900|1910|1920|1930&chxt=y,x&chbh=a&chs=300x225&cht=bvg&chco=A2C180&chd=t:${values}&chtt=Vertical+bar+chart' width='300' height='225' alt='Vertical bar chart' />"
        }
    }

    /**
     * Draw elements for taxa breakdown chart
     */
    def taxonChart = { attrs ->
        //println "taxonChart records link"
        //println buildRecordsUrl(attrs.uid)
        out <<
            "<div id='taxonRecordsLink' style='visibility:hidden;'>" +
            " <span id='viewRecordsLink' class='taxonChartCaption'><a class='recordsLink' href='${buildRecordsUrl(attrs.uid)}'>View all records</a></span><br/>" +
            "</div>" +
            "<div id='taxonChart'>" +
            " <img class='taxon-loading' alt='loading...' src='${resource(dir:'images/ala',file:'ajax-loader.gif')}'/>" +
            "</div>" +
            "<div id='taxonChartCaption' style='visibility:hidden;'>" +
            " <span class='taxonChartCaption'>Click a slice or legend to drill into a group.</span><br/>" +
            " <span id='resetTaxonChart' onclick='resetTaxonChart()'></span>&nbsp;" +
            " <div class='taxonCaveat'><span class='asterisk-container'><a href='${ConfigurationHolder.config.ala.baseURL}/about/progress/wrong-classification/'>Learn more about classification errors</a>&nbsp;</span></div>" +
            "</div>"

        /*out << '<div id="taxonChart">\n' +
                '<img class="taxon-loading" alt="loading..." src="' + resource(dir:'images/ala',file:'ajax-loader.gif') + '"/>\n' +
                '</div>\n' +
                '<div id="taxonChartCaption" style="visibility:hidden;">\n' +
                '<span class="taxonChartCaption">Click a slice to drill into a group.<br/>Click a legend colour patch<br/>to view records for a group.</span><br/>\n' +
                '<span id="resetTaxonChart" onclick="resetTaxonChart()"></span>&nbsp;\n' +
                '<div class="taxonCaveat"><span class="asterisk-container"><a href="http://www.ala.org.au/about/progress/wrong-classification/">Learn more about classification errors</a>&nbsp;</span></div>\n' +
                '</div>\n'*/
    }

    /**
     * Draw elements for decade breakdown chart
     */
    def decadeChart = {
        out << """                <div id="decadeChart">
                  <img class="decade-loading alt="loading..." src='""" + resource(dir:'images/ala',file:'decade-loader.gif') + """'/>
                </div>
                <div id="decadeChartCaption">
                  <span style="visibility:hidden;" class="decadeChartCaption">Click a column to view records for that decade.</span>
                </div>
        """
    }
    
    def homeLink = {
        out << '<a class="home" href="' + createLink(uri:"/manage") + '">Home</a>'
    }

    def returnLink = { attrs ->
        if (attrs.uid) {
            def pg = ProviderGroup._get(attrs.uid)
            if (pg) {
                out << link(class: 'return', controller: controllerFromUid(uid: attrs.uid), action: 'show', id: pg.uid) {'Return to ' + pg.name}
            }
        }
    }
    
    def partner = { attrs->
        if (attrs.test) {
            out << "<span class='partner follow'>Atlas Partner</span>"
        }
    }

    def tickOrCross = { attrs, body ->
        def trueText = body()
        def falseText = body()
        def split = body().toString().tokenize("|")
        if (split.size() == 2) {
            trueText = split[0]
            falseText = split[1]
        }
        if (attrs.test) {
            out << "<span class='tick'>${trueText}</span>"
        }
        else {
            out << "<span class='cross'>${falseText}</span>"
        }
    }

    def institutionCodes = { attrs ->
        def instCodes = attrs.collection.getListOfInstitutionCodesForLookup()
        switch (instCodes.size()) {
            case 0: out << "no institution code"; break
            case 1: out << "institution code = " + instCodes[0]; break
            default: out << "institution codes: " + instCodes.join(', ')
        }
    }
    
    def collectionCodes = { attrs ->
        // handle special case of ANY collection code
        if (attrs.collection?.providerMap?.matchAnyCollectionCode) {
            out << "any collection code"
        } else {
            def collCodes = attrs.collection.getListOfCollectionCodesForLookup()
            switch (collCodes.size()) {
                case 0: out << "no collection code"; break
                case 1: out << "collection code = " + collCodes[0]; break
                default: out << "collection codes: " + collCodes.join(', ')
            }
        }
    }

    def showOrEdit = { attrs ->
        def pg = attrs.entity
        if (pg) {
            out << link(controller: 'public', action:'show', id:pg.uid) {pg.name}
            out << " <span class='editLink'>(" + link(controller:pg.urlForm(), action:'show', id:pg.uid) {'edit'} + ")</span>"
        }
    }

    def reportClassification = { attrs ->
        def filter = attrs.filter
        if (filter) {
            // if filter specified: return tick if present
            if (Classification.matchKeywords(attrs.keywords, filter)) {
                out << "<img src='" + resource(dir:'images/ala', file:'olive-tick.png') + "'/>"
            }
        } else {
            // otherwise return classification in priority order
            if (Classification.matchKeywords(attrs.keywords, 'plants')) {
                out << 'herbarium'
            } else
            if (Classification.matchKeywords(attrs.keywords, 'entomology')) {
                out << 'entomology'
            } else
            if (Classification.matchKeywords(attrs.keywords, 'fauna')) {
                out << 'fauna'
            } else
            if (Classification.matchKeywords(attrs.keywords, 'microbes')) {
                out << 'microbial'
            }
        }
    }

    def tick = { attrs ->
        if (attrs.isTrue) {
            out << "<img src='" + resource(dir:'images/ala', file:'olive-tick.png') + "'/>"
        }
    }

    /**
     * Calculates a noun for a phrase like "holds 13,000 specimens" where specimens may be replaced by cultures
     * or some other appropriate noun.
     * @param types the list of collection types
     */
    def nounForTypes = {attrs ->
        def nouns = []
        if (attrs.types =~ "seedbank") {
            nouns << "accessions"
        }
        if (attrs.types =~ "preserved") {
            nouns << "specimens"
        }
        if (attrs.types =~ "cellcultures" || attrs.types =~ "living") {
            nouns << "cultures"
        }
        if (attrs.types =~ "genetic") {
            nouns << "samples"
        }
        if (!nouns) {
            nouns << "specimens"  // default
        }
        out << nouns.join(" and ")
    }
    
    /**
     * Adds site context to page title.
     */
    def pageTitle = { attrs, body ->
        out << "${body()} | Natural History Collections | Atlas of Living Australia"
    }

    /**
     * Returns lowercase past tense version of the change event.
     * Bolds the result for insert and delete if highlightInsertDelete is true.
     */
    def changeEventName = { attrs ->
        switch (attrs.event) {
            case 'UPDATE': out << 'updated'; break
            case 'INSERT': out << (attrs.highlightInsertDelete ? '<b>inserted</b>' : 'inserted'); break
            case 'DELETE': out << (attrs.highlightInsertDelete ? '<b>deleted</b>' : 'deleted'); break
        }
    }

    /**
     * Returns a string representation of the value suitable for short display such as in change logs.
     */
    def cleanString = { attrs ->
        def text = attrs.value
        println text
        // detect json array of strings
        if (text && text.size() > 1 && text[0..1] == '["' && text[text.size()-2..text.size()-1] == '"]') {
            def list = JSON.parse(text.toString())
            String str = ""
            list.each {str += "${it}, "}
            text = str.size() > 3 ? str[0..str.size()-3] : ""
        }
        // detect json array of objects
        if (text && text.size() > 1 && text[0..1] == '[{' && text[text.size()-2..text.size()-1] == '}]') {
            def list = JSON.parse(text.toString())
            String str = ""
            list.each {
                def obj = JSON.parse(it.toString())
                // handle subcollections explicitly (so we can order pairs sensibly)
                if (attrs.field == "subCollections") {
                    str += "name = ${obj.name}"
                    if (obj.description) {
                        str += "; desc = ${obj.description}"
                    }
                    str += "<br/>"
                }
                else {
                    obj.each {k, v ->
                        str += k.toString() + " = " + v.toString() + "<br/>"
                    }
                }
            }

            text = str.size() > 7 ? str[0..str.size()-6] : ""
        }

        out << "<span class='${attrs.class}'>" + text + "</span>"
    }

    /**
     * Returns the short class name from a fully-qualified class name.
     */
    def shortClassName = {attrs ->
        if (attrs.className) {
            def lastDot = attrs.className.lastIndexOf('.')
            if (lastDot > -1) {
                out << attrs.className[lastDot+1..attrs.className.size()-1]
            }
            else {
                out << attrs.className
            }
        }
    }

    /**
     * Returns the controller name for the specified class name.
     *
     * Class name may be fully qualified or short.
     */
    def controllerFromClassName = {attrs ->
        def clazz = shortClassName(className: attrs.className)
        if (clazz) {
            clazz = clazz[0].toLowerCase() + clazz[1..clazz.size()-1]
        }
        out << clazz
    }

    /**
     * Returns the controller name for the specified uid.
     */
    def controllerFromUid = {attrs ->
        if (attrs.uid?.size() > 2) {
            switch (attrs.uid[0..1]) {
                case 'co': out << 'collection'; break
                case 'in': out << 'institution'; break
                case 'dp': out << 'dataProvider'; break
                case 'dr': out << 'dataResource'; break
                case 'dh': out << 'dataHub'; break
            }
        }
    }

    /**
     * Bolds the name part of an email address, ie all before the @.
     */
    def boldNameInEmail = {attrs ->
        if (attrs.name) {
            def amp = attrs.name.indexOf('@')
            if (amp > -1) {
                out << "<b>" + attrs.name[0..amp-1] + "</b>" + attrs.name[amp..attrs.name.size()-1]
            }
            else {
                out << "<b>" + attrs.name + "</b>"
            }
        }
    }

    /**
     * Tailors the descriptive noun for an institution based on it's type.
     */
    def institutionType = {attrs ->
        if (attrs.inst?.institutionType == "governmentDepartment") {
            out << 'department'
        }
        else {
            out << 'institution'
        }
    }

    def progressBar = {attrs ->
        def percent = attrs.percent
        def initial = -120
        def imageWidth = 240
        def eachPercent = (imageWidth/2)/100
        def offset = initial + (percent * eachPercent)
        if (percent) {
            out << "<img id='progressBar' src='" + resource(dir:'images', file:'percentImage.png') +
                    "' alt='" + formatPercent(percent:percent) +
                    "%' class='no-radius percentImage1' style='background-position: ${offset}px 0pt; '>"
        }
    }

    /**
     * Writes a para with date last updated.
     *
     * @param date
     */
    def lastUpdated = {attrs ->
        if (attrs.date) {
            out << "<p class='lastUpdated'>" + g.message(code: "metadata.last.updated", args: []) + " ${attrs.date}</p>"
        }
    }

    /**
     * Write the appropriate breadcrumb trail.
     *
     * Checks the config for skin to choose the correct hierarchy.
     *
     * @attr home the root of the page - defaults to collections
     * @attr atBase true if the page is the base page of the root (no link is added)
     */
    def breadcrumbTrail = {attrs ->

        def hereLink = ""
        def topLevelLink = ""
        switch (attrs.home) {
            case 'dataSets':
                hereLink = attrs.atBase == 'true' ?
                    "Datasets" :
                    link(controller:'public', action:'dataSets') {"List"}
                break
            case 'dashboard':
                hereLink = attrs.atBase == 'true' ?
                    "Dashboard" :
                    link(controller:'dashboard', action:'index') {"Dashboard"}
                break
            default:
                hereLink = attrs.atBase == 'true' ?
                    "Collections" :
                    link(controller:'public', action:'map') {"Collections"}
        }
        if (grailsApplication.config.skin.includeBaseUrl) {
            out << "<a href='${grailsApplication.config.ala.baseURL}'>Home</a> <span class=\"icon icon-arrow-right\"></span> "
        }
        out <<   hereLink
    }

    def pageOptionsLink = {attrs, body ->
        out << "<a href='#optionsText' class='current'>"
        out << body()
        out << "</a>"
    }

    def pageOptionsPopup = {attrs ->
        out << """<div style="display:none; text-align: left;">\n"""
        out << """<div id="optionsText" style="text-align: left;">\n"""
        out << """<p class='pageOptions' width="100%" style="color:#666;padding-bottom:5px;text-align:center">Page options</p>\n"""
        out << """<p class='editLink' style="padding-left:30px;text-indent:-30px;">\n"""
        out << "<img class='editImg' style='margin-right:5px;vertical-align:middle' src='${resource(dir:'images/ala',file:'edit.png')}'/>\n"
        out << link(controller:attrs.instance.urlForm(), action:'show', id:attrs.instance.uid) {"Edit metadata"}
        out << " for this ${attrs.instance.textFormOfEntityType(attrs.instance.uid)}. You need<br/>appropriate authorisation to do this. You will<br/>be asked to log in if you are not already.</p>\n"
        def providers = attrs.instance.listProviders()
        if (attrs.instance instanceof Collection && attrs.instance.institution) {
            providers += attrs.instance.institution?.listProviders()
        }
        if (providers) {
            boolean first = true
            providers.each {
                // only write the header if we have at least one resource
                if (first) {
                    out << "<p class='viewList' style='margin-top:5px;'>View data sources<br/><ul>\n"
                    first = false
                }
                def provider = ProviderGroup._get(it)
                if (provider) {
                    out << "<li>" +
                            link(action:'show',id:provider.uid) {provider.name} +
                            "</li>\n"
                }
            }
            out << "</ul></p>\n"
        }
        def consumers = attrs.instance.listConsumers()
        if (attrs.instance instanceof DataResource && attrs.instance.dataProvider) {
            consumers += attrs.instance.dataProvider?.listConsumers()
        }
        if (consumers) {
            boolean first = true
            consumers.each {
                // only write the header if we have at least one resource
                if (first) {
                    out << "<p class='viewList' style='margin-top:5px;'>View data consumers<br/><ul>\n"
                    first = false
                }
                def consumer = ProviderGroup._get(it)
                if (consumer) {
                    out << "<li>" +
                            link(action:'show',id:consumer.uid) {consumer.name} +
                            "</li>\n"
                }
            }
            out << "</ul></p>\n"
        }
        out << "</div>\n"
        out << "</div>\n"
    }

    def linkLengthLimit = 42

    def wrappedLink = {attrs, body ->
        def text = body().toString()
        if (text.size() > linkLengthLimit) {
            def chunks = splitText(text)
            out << "<a href='${attrs.href}' rel='nofollow' target='_blank'>${chunks[0]}</a><br/><a href='${attrs.href}' class='external' target='_blank'>${chunks[1]}</a>"
        } else {
            out << "<a href='${attrs.href}' rel='nofollow' class='external' target='_blank'>${text}</a>"
        }
    }

    private String[] splitText(text) {
        def linkPart = ""
        def rest = ""
        // find whitespace within the length limit
        def words = text.tokenize()
        boolean broken = false
        words.each {
            if (!broken && (linkPart.size() + it.size()) < linkLengthLimit) {
                linkPart += (linkPart ? " " + it : it)
            } else {
                broken = true
                rest += (rest ? " " + it : it)
            }
        }
        return [linkPart, rest] as String[]
    }

    /**
     * Outputs a button to link to the edit page if the user is authorised to edit.
     *
     * @params uid the entity uid
     * @params action optional action - defaults to edit
     * @params controller optional controller - defaults to not specified (current)
     * @params id optional id to edit if it is different to the uid
     * @params any other attrs are passed to link as url params
     * @params notAuthorisedMessage written in place of button if not authorised - defaults to blank
     * @body the label for the button - defaults to 'Edit' if not specified
     */
    def editButton = { attrs, body ->
        if (isAuthorisedToEdit(attrs.uid, request.getUserPrincipal()?.attributes?.email)) {
            def paramsMap
            // anchor class
            paramsMap = [class:'edit btn']
            // action
            paramsMap << [action: (attrs.containsKey('action')) ? attrs.remove('action').toString() : 'edit']
            // optional controller
            if (attrs.containsKey('controller')) { paramsMap << [controller: attrs.remove('controller').toString()] }
            // id of target
            paramsMap << [id: (attrs.containsKey('id')) ? attrs.remove('id').toString() : attrs.uid]
            attrs.remove('uid')
            // add any remaining attrs as params
            paramsMap << [params: attrs]

            out << "<div><span class='buttons'>"
            out << link(paramsMap) {body() ?: 'Edit'}
            out << "</span></div>"
        } else {
            out << attrs.notAuthorisedMessage
        }
    }

    def showRecordsExceptions = {attrs ->
        def exceptions = attrs.exceptions
        println exceptions
        if (exceptions) {
           out << '<div class="child-institutions">'
           switch (exceptions.listType) {
           case 'excludes':
               out << '<span>Note that totals and charts do not include records provided by the related institution' +
                       (exceptions.childInstitutions.size() > 1 ? 's' : '') + ':</span>'
               out << '<ul>'
               exceptions.childInstitutions.each {inst ->
                   def collections = inst.listCollections()
                   switch (collections?.size()) {
                       case 0: break
                       case 1:
                           out << "<li>the ${link(controller: 'public', action: 'show', id: inst.uid) {inst.name}} which includes "
                           out << "the ${link(controller: 'public', action: 'show', id: collections[0].uid) {collectionName(name: collections[0].name)}}.</li>"
                           break;
                       default:
                           out << "the " + link(controller: 'public', action: 'show', id: inst.uid) {inst.name} + " which includes"
                           out << "<ul>"
                           collections.eachWithIndex { co, index ->
                               out << "<li>the ${link(controller: 'public', action: 'show', id: co.uid) {collectionName(name: co.name)}}"
                               out << (index == collections.size() - 1 ? "." : " and")
                               out << "</li>"
                           }
                           out << "</ul>"
                           break;
                   }
               }
               out << "</ul>"
               break;
           case 'excludes-all':
               out << '<span>Records for collections supported by this institution can be viewed on the pages of the related institution' +
                       (exceptions.childInstitutions.size() > 1 ? 's' : '') + ':</span>'
               out << '<ul>'
               exceptions.childInstitutions.each {inst ->
                   def collections = inst.listCollections()
                   switch (collections?.size()) {
                       case 0: break
                       case 1:
                           out << "<li>the ${link(controller: 'public', action: 'show', id: inst.uid) {inst.name}} which includes "
                           out << "the ${link(controller: 'public', action: 'show', id: collections[0].uid) {collectionName(name: collections[0].name)}}.</li>"
                           break;
                       default:
                           out << "the " + link(controller: 'public', action: 'show', id: inst.uid) {inst.name} + " which includes"
                           out << "<ul>"
                           collections.eachWithIndex { co, index ->
                               out << "<li>the ${link(controller: 'public', action: 'show', id: co.uid) {collectionName(name: co.name)}}"
                               out << (index == collections.size() - 1 ? "." : " and")
                               out << "</li>"
                           }
                           out << "</ul>"
                           break;
                   }
               }
               out << "</ul>"
               break;
           case 'includes':
               out << '<span>Note that totals and charts only include records from'
               switch (exceptions.includes?.size()) {
                   case 0: break
                   case 1:
                       out << " the ${link(controller: 'public', action: 'show', id: exceptions.includes[0].key) {collectionName(name: exceptions.includes[0].value)}}.</span>"
                       break;
                   default:
                       out << ":</span><ul>"
                       exceptions.includes.eachWithIndex { key, value, index ->
                           out << "<li>the ${link(controller: 'public', action: 'show', id: key) {collectionName(name: value)}}"
                           out << (index == exceptions.includes.size() - 1 ? "." : " and")
                           out << "</li>"
                       }
                       out << "</ul>"
                       break;
               }
               out << "<span>Totals and charts for the other collections can be viewed in the related institution" +
                       (exceptions.childInstitutions.size() > 1 ? "s" : "") + ":</span><ul>"
               exceptions.childInstitutions.each { inst ->
                   out << "<li>" + link(controller: 'public', action: 'show', id: inst.uid) {inst.name} + "</li>"
               }
               out << "</ul>"
           }
          out << "</div>"
        }
    }

    def entityIndicator = { attrs ->
        def ind = ""
        if (attrs.entity) {
            switch (attrs.entity.class) {
                case Collection.class: ind = "(C)"; break
                case Institution.class: ind = "(I)"; break
                case DataProvider.class: ind = "(P)"; break
                case DataResource.class: ind = "(R)"; break
                case DataHub.class: ind = "(H)"; break
            }
        }
        out << ind
    }

    def viewPublicLink = { attrs, body ->
        out << link(class:"preview", controller:"public", action:'show', id:attrs.uid) { "<img class='ala' alt='ala' src='${resource(dir:"images", file:"favicon.gif")}'/> View public page" }
    }

    def jsonSummaryLink = { attrs, body ->
        // have to use this method rather than 'link' so we can specify the accept format as json
        out << "<a class='json' href='${resource(dir:"lookup",file:"summary/${attrs.uid}.json")}'><img class='json' alt='summary' src='${resource(dir:"images", file:"json.png")}'/> View summary</a>"
    }

    def jsonDataLink = { attrs, body ->
        def uri = "${ConfigurationHolder.config.grails.serverURL}/ws/${ProviderGroup.urlFormFromUid(attrs.uid)}/${attrs.uid}.json"
        // have to use this method rather than 'link' so we can specify the accept format as json
        out << "<a class='json' href='${uri}'><img class='json' alt='json' src='${resource(dir:"images", file:"json.png")}'/> View raw data</a>"
    }

    def viewLink = {attrs, body ->
        out << link(class:"preview", controller:"public", action:'show', id:attrs.uid) { body() }
    }

    def editLink = {attrs, body ->
        out << link(class:"preview", controller:ProviderGroup.urlFormFromUid(attrs.uid), action:'show', id:attrs.uid) { body() }
    }

    def displayLicenseType = {attrs ->
        def license = attrs.type
        if (license) {
            def display = DataResource.ccDisplayList.find { it.type == license }
            def image
            def link
            def version = attrs.version ?: '3.0'
            switch (license) {
                case "CC BY":
                    image = "http://i.creativecommons.org/l/by/${version}/88x31.png";
                    link = "http://creativecommons.org/licenses/by/${version}/au/"
                    break
                case "CC BY-NC":
                    image = "http://i.creativecommons.org/l/by-nc/${version}/88x31.png";
                    link = "http://creativecommons.org/licenses/by-nc/${version}/au/"
                    break
                case "CC BY-SA":
                    image = "http://i.creativecommons.org/l/by-sa/${version}/88x31.png";
                    link = "http://creativecommons.org/licenses/by-sa/${version}/au/"
                    break
                case "CC BY-NC-SA":
                    image = "http://i.creativecommons.org/l/by-nc-sa/${version}/88x31.png";
                    link = "http://creativecommons.org/licenses/by-nc-sa/${version}/au/"
                    break
                default:
                    image = null
                    link = null
            }
            def imageHtml = "<a rel='license' target='_blank' href='${link}'><img class='ccimage no-radius' src='${image}' alt='Creative Commons License' style='border:none;' height='31' width='88'></a>"
            if (attrs.imageOnly && image) {
                out << imageHtml
            }
            else if (image) {
                out << "${display.display?:'No licence specified'} ${attrs.version?:''} Australia (${license}) ${imageHtml}"
            }
            else {
                out << "${display.display?:'No licence specified'} ${attrs.version?:''} Australia (${license})"
            }
        }
    }

    def showConnectionParameters = { attrs ->
        // see if we have a protocol
        if (attrs.connectionParameters?.toString()) {

            // create a table to display them
            out << "<dl class='dl-horizontal'>"

            // parse the storage string
            def cp = JSON.parse(attrs.connectionParameters.toString())

            // load the profile of the selected protocol
            def profile = metadataService.getConnectionProfile(cp.protocol)

            // display the protocol
            out << "<dt>Protocol:</dt><dd>${profile.display}</dd>"

            // display each of the protocol's parameters
            profile.params.each {pp ->
                def value
                if (pp.paramName == "termsForUniqueKey") {
                    // show as comma separated list
                    value = cp."${pp.paramName}".collect {it}.join(', ') as String
                }
                else if (cp."${pp.paramName}" instanceof List) {
                    // show as list
                    value = cp."${pp.paramName}".join('<br/>');
                }
                else {
                    // encode any control characters
                    value = encodeControlChars(cp."${pp.paramName}")
                }

                if(pp.paramName == "url"){
                    out << "<dt id=\"dataURL\">Data URL</dt><dd><a href=\"" + metadataService.convertPath(value)  + "\"> "+metadataService.convertPath(value) +"</a></dd>"
                }

                out << "<dt id=\"${pp.paramName}\">${pp.display}:</dt><dd>" + (value ?: 'Not supplied') + "</dd>"
            }
            out << "</dl>"
        }
        else {
            out << "none"
        }
    }

    /**
     * Encodes a string as a url if it contains any control characters
     * @param str string to encode
     * @return
     */
    String encodeControlChars(str) {
        // make sure it's a string and not something else like a JSONArray
        if (!(str instanceof String)) {
            return str
        }
        def outStr = ""
        str.each {
            switch (it) {
                case PP.HT_CHAR: outStr += 'HT'; break
                case PP.LF_CHAR: outStr += 'LF'; break
                case PP.VT_CHAR: outStr += 'VT'; break
                case PP.FF_CHAR: outStr += 'FF'; break
                case PP.CR_CHAR: outStr += 'CR'; break
                default: outStr += it
            }
        }
        return outStr
    }

    /**
     * Builds the html for editing connection parameters.
     */
    def connectionParameters = { attrs ->
        // see if we have a protocol
        def cp = null
        def protocol = 'none'
        if (attrs.connectionParameters.toString()) {
            cp = JSON.parse(attrs.connectionParameters.toString())
            protocol = cp.protocol
        }

        // show the protocol selector
        out << """<tr class="prop">
            <td valign="top" class="name">
              <label for="protocol">Protocol</label>
            </td>
            <td valign="top" class="value">""" +
                select(id:'protocolSelector',
                       name:"protocol",
                       from:metadataService.getConnectionProfilesAsList(),
                       value:protocol,
                       optionValue:'display',
                       optionKey:'name',
                       onchange:'changeProtocol()') +
                """<cl:helpText code="dataResource.connectionParameters.protocol"/>
              </td>
              <cl:helpTD/>
        </tr>"""

        // create the widgets for each protocol (profile)
        metadataService.getConnectionProfilesAsList().each {
            // is this the selected protocol?
            boolean selected = (it.name == cp?.protocol)
            String hidden = selected ? '' : "display:none;"

            it.params.each { ppName ->

                def pp = metadataService.getConnectionParameter(ppName)

                // get value from object
                def displayedValue = cp?."${pp.paramName}"?:""

                // inject default if no value
                if (!displayedValue && pp.defaultValue) {
                    displayedValue = pp.defaultValue
                }

                // unravel any JSON lists
                if (displayedValue instanceof JSONArray) {
                    displayedValue = displayedValue.collect {it}.join(', ') as String
                }

                // handle unprintable chars
                if (pp.type == 'delimiter') {
                    displayedValue = encodeControlChars(displayedValue)
                }

                def attributes = [name:pp.paramName, value:displayedValue]
                if (!selected) {
                    attributes << [disabled:true]
                }
                if (pp.paramName == "termsForUniqueKey") {
                    // handle terms specially
                    out << """<tr class='labile' id="${it.name}" style="${hidden}">
                                  <td class='be-careful' colspan='2'>
                                        <span class='label label-important'>Don't change the following terms unless you know what you are doing.
                                        Incorrect values can cause major devastation.</span>
                                  </td>
                               </tr>
                        <tr class="prop labile" style="${hidden}" id="${it.name}">
                        <td valign="top" class="name"
                          <label for="termsForUniqueKey">${pp.display}</label>
                        </td>
                        <td valign="top" class="value">""" +
                            textField(attributes) +
                            helpText(code:'dataResource.termsForUniqueKey') +
                        "</td>" +  helpTD() + "</tr>"
                } else {
                    // all others
                    def widget
                    switch (pp.type) {
                        case 'textArea': widget = 'textArea'; break
                        case 'boolean': widget = 'checkBox'; break
                        default: widget = 'textField'; break
                    }
                    out << """<tr class="prop labile" style="${hidden}" id="${it.name}">
                        <td valign="top" class="name"
                          <label for="${pp.paramName}">${pp.display}</label>
                        </td>
                        <td valign="top" class="value">""" +
                            "${widget}"(attributes) +
                            helpText(code:'dataResource.' + pp.paramName) +
                        "</td>" +  helpTD() + "</tr>"
                }
            }
        }

    }

    def lastChecked = { attrs ->
        if (attrs.date) {
            out << """<span id="updated">This resource was last checked for updated data on
                <b>${new SimpleDateFormat("dd MMM yyyy").format(attrs.date)}</b>.</span>"""
        }
    }

    def dataCurrency = { attrs ->
        if (attrs.date) {
            out << """<span id="currency">The most recent data was published on
                <b>${new SimpleDateFormat("dd MMM yyyy").format(attrs.date)}</b>.</span>"""
        }
    }

    def shortPermissionsDocument = { attrs ->
        if (attrs.url) {
            String url = attrs.url
            // only show document name
            def bits = url.tokenize('/')
            // grab the text after the last /
            def name = bits.size() ? bits[-1] : url

            def limit = 17
            
            if (name.size() > limit) {
                def dotBits = name.tokenize('.')
                def head = dotBits.size() > 1 ? dotBits[0..-2].join('.') : name
                def tail = dotBits.size() > 1 ? dotBits[-1] : ""
                name = (head.size() > 8 ? head[0..8] : head) + '...' + tail
            }
            out << name
        }
    }

    /**
     * Text to represent the permissions document type.
     *
     * @attr type the full type value
     */
    def shortPermissionsDocumentType = {attrs ->
        if (attrs.type) {
            switch (attrs.type) {
                case 'Email': out << 'Email'; break
                case 'Data Provider Agreement': out << 'DPA'; break
                case 'Web Page': out << 'Web'; break
                case 'Other': out << 'Other'; break
                default: out << 'unknown'
            }
        }
    }

    /**
     * Text to represent the flags for data provider agreements.
     *
     * @attr filed the filed flag
     * @attr risk the riskAssessment flag
     * @attr brief controls the representation
     */
    def dpaStatus = { attrs ->

        def str = []
        if (attrs.filed) {
            str.add attrs.brief ? "F" : "Agreement filed"
        }
        if (attrs.risk) {
            str.add attrs.brief ? "R" : "Risk assessment completed"
        }
        out << str.join(attrs.brief ? ' ' : ', ')
    }

    /**
     * Text appropriate to the type of contribution.
     *
     * @attr resourceType the typeof resource
     * @attr status the state of progress in integration the resource
     * @attr tag the tag to enclose the text
     */
    def dataResourceContribution = { attrs ->
        def startTag = attrs.tag ? "<${attrs.tag}>" : ""
        def endTag = attrs.tag ? "</${attrs.tag}>" : ""
        if (attrs.resourceType == 'website' && attrs.status == 'dataAvailable') {
            out << startTag + "This website provides content for Atlas species pages." + endTag
        }
        else if (attrs.resourceType == 'website' && attrs.status == 'linksAvailable') {
            out << startTag + "Links to this website appear on appropriate Atlas species pages." + endTag
        }
        else if (attrs.resourceType == 'document' && attrs.status == 'dataAvailable') {
            out << startTag + "Documents from this source provide content for Atlas species pages." + endTag
        }
        else if (attrs.resourceType == 'document' && attrs.status == 'linksAvailable') {
            out << startTag + "Links to this document appear on appropriate Atlas species pages." + endTag
        }
    }

    /**
     * List of content types.
     *
     * @attr types json list of string
     */
    def contentTypes = { attrs ->
        if (attrs.types) {
            def list = JSON.parse(attrs.types as String).collect {it.toString()}
            out << '<p>Includes: ' + list.join(', ') + '.</p>'
        }
    }

    def taxonomicRangeDescription = { attrs ->
        if (attrs.obj) {
            def obj = JSON.parse(attrs.obj)
            if (attrs.key) {
                obj = obj[attrs.key]
            }
            def list = obj.collect {it.toString()}
            out << '<p><span class="category">Range:</span> '
            if(list){
                out <<  list.join(', ')
            } else {
                out <<  'Not specified'
            }
            out <<  '</p>'
        }
    }

    def externalLink = { attrs ->
        def href = attrs.href
        if (!attrs.href?.startsWith("http://")) {
            href = "http://" + href
        }
        out << """<a class="external_icon" target="_blank" href="${href}">${attrs.href}</a>"""
    }

    def toJson = { attrs ->
        def json = attrs.obj as JSON
        println json
        out << json.toString()
    }

    def selected = {
        if (request.requestURL =~ 'datasets') {
            out << ""
        }
        else {
            out << ' selected'
        }
    }
}
