package au.org.ala.collectory
import grails.converters.JSON
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.xml.sax.SAXException

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import java.text.DateFormat
import java.text.SimpleDateFormat

class DataController {

    def crudService, authService, emlRenderService
    
    def index = { }

    def metadataService

    /** make sure that uid params point to an existing entity and json is parsable **/
    def beforeInterceptor = this.&check

    def check() {
        def uid = params.uid
        if (uid) {
            // it must exist
            def pg = uid.startsWith('drt') ? TempDataResource.findByUid(uid) : ProviderGroup._get(uid)
            if (pg) {
                params.pg = pg
                // if entity is specified, the instance must be of type entity
                if (params.entity && pg.urlForm() != params.entity) {
                    // exists but wrong type (eg /dataHub/dp20)
                    notFound "entity with uid = ${uid} is not a ${params.entity}"
                    return false
                }
            } else {
                // doesn't exist
                notFound "no entity with uid = ${uid}"
                return false
            }
        }
        if (request.method == 'POST' || request.method == "PUT" || request.method == 'DELETE') {
            if (request.getContentLength() == 0 && request.method != 'DELETE') {
                // no payload so return OK as entity exists (if specified)
                success "no post body"
                return false
            }
            try {
                params.json = request.JSON
            } catch (Exception e) {
                println "exception caught ${e}"
                // allow empty body
                if (request.getContentLength() > 0) {
                    badRequest 'cannot parse request body as JSON'
                    return false
                }
            }
            // all data modifications by ws require a valid api key in the body
            if (!params.json) {
                unauthorised()
                return false
            }
            def keyCheck = authService.checkApiKey(params.json.api_key)
            if (!keyCheck.valid) {
                unauthorised()
                return false
            }
            // inject the user name into the session so it can be used by audit logging if changes are made
            session.username = keyCheck.app ?: (keyCheck.userEmail ?: params.json.user)
        }
        return true
    }

    /******* Web Services Catalogue *******/

    def catalogue = { }

    /***** CRUD RESTful services ********/

    /**
     * format for RFC 1123 date string -- "Sun, 06 Nov 1994 08:49:37 GMT"
     */
    final static String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
      * DateFormat to be used to format dates
      */
    final static DateFormat rfc1123Format = new SimpleDateFormat(RFC1123_PATTERN)
    static {
        rfc1123Format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    def cacheAwareRender = { content, last, eTag ->
        if (last) {
            addLastModifiedHeader last
        }
        if (eTag) {
            addETagHeader eTag
        }
        render content
    }

    def renderJson = { json, last, eTag ->

        def content = json

        if (params.callback) {
            content = "${params.callback}(${json})"
        }

        if (last) {
            addLastModifiedHeader last
        }
        if (eTag) {
            addETagHeader eTag
        }
        render(text:content, encoding:"UTF-8", contentType: "application/json")
    }

    def renderAsJson = {json, last, eTag ->
        renderJson(json as JSON, last, eTag)
    }

    def addLocation(relativeUri) {
        response.addHeader 'location', grailsApplication.config.grails.serverURL + relativeUri
    }

    def addContentLocation(relativeUri) {
        response.addHeader 'content-location', grailsApplication.config.grails.serverURL + relativeUri
    }

    def created = {clazz, uid ->
        addLocation "/ws/${clazz}/${uid}"
        render(status:201, text:'inserted entity')
    }

    def badRequest = {text ->
        render(status:400, text: text)
    }

    def success = { text ->
        render(status:200, text: text)
    }

    def notModified = {
        render(status: 304)
    }
    
    def notFound = { text ->
        render(status:404, text: text)
    }

    def notAllowed = {
        response.addHeader 'allow','POST'
        render(status:405, text: 'Only POST supported')
    }

    def unauthorised = {
        // using the 'forbidden' response code here as 401 causes the client to ask for a log in
        render(status:403, text: 'You are not authorised to use this service')
    }

    /**
     * Should be added for any uri that returns multiple formats based on content negotiation.
     * (So the content can be correctly cached by proxies.)
     */
    def addVaryAcceptHeader = {
        response.addHeader HttpHeaders.VARY, HttpHeaders.ACCEPT
    }

    def addLastModifiedHeader = { when ->
        response.addHeader HttpHeaders.LAST_MODIFIED, rfc1123Format.format(when)
    }

    def addETagHeader = { eTag ->
        response.addHeader HttpHeaders.ETAG, '"' + eTag + '"'
    }

    private capitalise(word) {
        switch (word?.size()) {
            case 0: return ""
            case 1: return word[0].toUpperCase()
            default: return word[0].toUpperCase() + word [1..-1]
        }
    }

    /**
     * Update database from post/put data.
     *
     * If uid specified, it must exist -> update entity
     * Else -> insert entity
     *
     * @param entity - controller form of domain class, eg dataProvider
     * @param uid - optional uid of an instance of entity
     * @param pg - optional instance specified by uid (added in beforeInterceptor)
     * @param json - the body of the request
     */
    def saveEntity = {
        def pg = params.pg
        def obj = params.json
        def urlForm = params.entity
        def clazz = capitalise(urlForm)

        if (pg) {
            // check type
            if (pg.getClass().getSimpleName() == clazz) {
                // update
                crudService."update${clazz}"(pg, obj)
                if (pg.hasErrors()) {
                    badRequest pg.errors
                } else {
                    addContentLocation "/ws/${pg.urlForm()}/${params.uid}"
                    success "updated ${clazz}"
                }
            } else {
                badRequest "entity with uid = ${params.uid} is not ${clazz == 'Institution'? 'an' : 'a'} ${clazz}"
            }
        } else {
            // doesn't exist insert
            pg = crudService."insert${clazz}"(obj)
            if (pg.hasErrors()) {
                badRequest pg.errors
            } else {
                created pg.urlForm(), pg.uid
            }
        }
    }

    /**
     * Define some variations on the level of detail returned for lists.
     */
    def brief = {[name: it.name, uri: it.buildUri(), uid: it.uid]}
    def summary = {[name: it.name, uri: it.buildUri(), uid: it.uid, logo: it.buildLogoUrl()]}

    def index() {
        def root = params.root
        def path = getPath(request.forwardURI, params.path)

        def basePath = grailsApplication.config.grails?.plugins?.fileserver?.paths?.get(root)
        File file = basePath ? fileService.loadFile(basePath, path) : null

        if (file) {
            log.debug("$root/$path, sending file: $file.absolutePath")
            response.outputStream << file.bytes
        } else {
            log.debug("$root/$path, file not found - dir: $basePath, file: $path")
            response.status = 404
        }
    }

    def serveFile = {
        def dirpath =  "/" + params.directory + "/"
        def idx = request.forwardURI.lastIndexOf(dirpath) + dirpath.length()
        def fullFileName = request.forwardURI.substring(idx)
        def file = new File(grailsApplication.config.repository.location.images + File.separator + params.directory, fullFileName)
        if(file.exists()){
            if(fullFileName.endsWith(".json")){
                response.setContentType("application/json")
            }
            response.outputStream << file.bytes
        } else {
            response.status = 404
        }
    }

    def fileDownload = {
        def dirpath =  "/" + params.directory + "/"
        def idx = request.forwardURI.lastIndexOf(dirpath) + dirpath.length()
        def fullFileName = request.forwardURI.substring(idx)
        def file = new File(grailsApplication.config.uploadFilePath + File.separator + params.directory, fullFileName)
        if(file.exists()){
            //set the content type
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment;filename=" + file.getName())
            response.outputStream << file.bytes
        } else {
            response.status = 404
        }
    }

    /**
     * Return JSON representation of specified entity
     * or list of entities if no uid specified.
     *
     * @param entity - controller form of domain class, eg dataProvider
     * @param uid - optional uid of an instance of entity
     * @param pg - optional instance specified by uid (added in beforeInterceptor)
     * @param summary - any non-null value will cause a richer summary to be returned for entity lists
     */
    def getEntity = {
        if (params.entity == 'tempDataResource') {
            forward(controller: 'tempDataResource', action: 'getEntity')
        } else {
            def urlForm = params.entity
            def clazz = capitalise(urlForm)
            if (params.pg) {
                // return specified entity
                addContentLocation "/ws/${urlForm}/${params.pg.uid}"
                def eTag = (params.pg.uid + ":" + params.pg.lastUpdated).encodeAsMD5()
                def entityInJson = crudService."read${clazz}"(params.pg)
                entityInJson = metadataService.convertAnyLocalPaths(entityInJson)
                response.setContentType("application/json")
                response.setCharacterEncoding("UTF-8")
                cacheAwareRender entityInJson, params.pg.lastUpdated, eTag
            } else {
                // return list of entities
                addContentLocation "/ws/${urlForm}"
                def domain = grailsApplication.getClassForName("au.org.ala.collectory.${clazz}")
                def list = domain.list([sort:'name'])
                list = filter(list)
                def last = latestModified(list)
                def detail = params.summary ? summary : brief
                def summaries = list.collect(detail)
                def eTag = summaries.toString().encodeAsMD5()
//                response.setCharacterEncoding("UTF-8")
                response.setContentType("application/json")
                renderAsJson summaries, last, eTag
            }
        }
    }

    /**
     * Return JSON representation of the counts of the specified entity
     * grouped by the specified property.
     *
     * @param entity - controller form of domain class, eg dataProvider
     * @param groupBy - name of the property to group by
     */
    def count = {

        // get list of entities
        def urlForm = params.entity
        def clazz = capitalise(urlForm)
        def domain = grailsApplication.getClassForName("au.org.ala.collectory.${clazz}")
        def list = domain.list()
        
        // suppress 'declined' data resources
        if (urlForm == 'dataResource' && params.public == "true") {
            list = list.findAll { it.status != 'declined' }
        }
        
        // init results with total
        def results = [total: list.size()]

        if (params.groupBy) {
            results.groupBy = params.groupBy
            def groups = [:]
            list.each {
                def value = it[params.groupBy]
                if (groups[value]) {
                    groups[value]++
                }
                else {
                    groups[value] = 1
                }
            }
            results.groups = groups
        }

        addContentLocation "/ws/${urlForm}/count"
        def last = latestModified(list)

        renderAsJson results, last, ""
    }

    /**
     * Supports caching. Tests whether the resource has been modified based on both last-modified
     * date and eTags. Compares current state of resource with the appropriate request headers.
     *
     * @param last the timestamp representing when the resource was last modified
     * @param eTag a calculated eTag for the resource
     * @return true if the resource has not been modified (wrt the request)
     */
    def isNotModified(last, eTag) {
        boolean modified = true
        // compare 'last modified' with 'if modified since'
        def since = request.getHeader(HttpHeaders.IF_MODIFIED_SINCE)
        if (last && since) {
            modified = rfc1123Format.parse(since) > last
        }
        // compare eTags
        def match = request.getHeader(HttpHeaders.IF_NONE_MATCH)
        if (eTag && match) {
            boolean tagsDifferent = match ? (eTag != match[1..-2]) : true
            modified &= tagsDifferent
        }
        return !modified
    }

    /**
     * Calculates the latest last modified date for a list of entities with that property.
     *
     * @param list
     * @return
     */
    def latestModified(list) {
        def latest = null
        list.each {
            if (it.lastUpdated > latest) {
                latest = it.lastUpdated
            }
        }
        return latest
    }

    /**
     * Filters a list based on query parameters.
     *
     * For each query param that is a property of the first member of the list,
     * the list is reduced to members who have the specified value for the property.
     *
     * @param list of entities
     * @return
     */
    def filter(list) {
        if (!list) return list
        params.each { key, value ->
            if (list[0].hasProperty(key)) {  // assumes a list of a single type
                list = list.findAll {
                    def propertyValue = it."${key}"
                    def paramValue = value
                    if (propertyValue instanceof Boolean) {
                        paramValue = (paramValue.toLowerCase() == 'true')
                    }
                    propertyValue == paramValue
                }
            }
        }
        return list
    }

    /**
     * Return headers as if GET had been called - but with no payload.
     *
     * @param entity - controller form of domain class, eg dataProvider
     * @param uid - optional uid of an instance of entity
     * @param pg - optional instance specified by uid (added in beforeInterceptor)
     */
    def head = {
        if (params.entity && params.pg) {
            addContentLocation "/ws/${params.pg.urlForm()}/${params.pg.uid}"
            addLastModifiedHeader params.pg.lastUpdated
            render ""
        }
    }

    def resolveNames = {
        def uids = params.uids.tokenize(',')
        def result = [:]
        uids.each {
            def pg = ProviderGroup._get(it)
            if (pg) {
                result << ["${pg.uid}": pg.name]
            }
        }
        renderAsJson result, null, result.toString().encodeAsMD5()
    }

    /********* delete **************************
     *
     */
    def delete = {
        if (ConfigurationHolder.config.deletesForbidden) {
            render(status:405, text:'delete is currently unavailable')
            return
        }
        // check role
        if (params.uid) {
            def pg = params.uid.startsWith('drt') ?
                TempDataResource.findByUid(params.uid) :
                ProviderGroup._get(params.uid)
            if (pg) {
                def name = pg.name
                pg.delete()
                def message = ['message':"deleted ${name}"]
                render message as JSON
            } else {
                def error = ['error': "no uid specified"]
                render error as JSON
            }
        }
    }

    /************ EML services *************/

    def eml = {
        if (params.id) {
            def pg = ProviderGroup._get(params.id)
            if (pg) {
                response.contentType = 'text/xml'
                def xml = emlRenderService.emlForEntity(pg)
                def error = ''
                if (params.validate) {
                    error = validate(xml)
                }
                if (error) {
                    render error
                } else {
                    //render xml
                    response.setContentType("text/xml")
                    response.setCharacterEncoding("UTF-8")
                    cacheAwareRender xml, pg.lastUpdated, xml.toString().encodeAsMD5()
                }
            } else {
                notFound 'no such entity ' + params.id
            }
        } else {
            badRequest 'you must specify an entity identifier (uid)'
        }
    }

    def validate(xml) {
        try {
            def factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
            def schema = factory.newSchema()
            //def schema = factory.newSchema(new URL("http://rs.gbif.org/schema/eml-gbif-profile/dev/eml.xsd"))
            def validator = schema.newValidator()
            validator.validate(new StreamSource(new StringReader(xml)))
        } catch (SAXException e) {
            return e.getLocalizedMessage()
        }
        return null
    }

    def validate = {
        if (params.id) {
            def pg = ProviderGroup._get(params.id)
            if (pg) {
                def xml = emlRenderService.emlForEntity(pg)
                def error = ''
                if (params.validate) {
                    error = validate(xml)
                }
                if (error) {
                    render error
                } else {
                    render 'valid'
                }
            } else {
                notFound 'no such entity ' + params.id
            }
        } else {
            // just do hubs to start
            int validCount = 0
            def invalid = []
            //DataHub.list().each {
            Collection.list().each {
                def xml = emlRenderService.emlForEntity(it)
                def error = ''
                if (params.validate) {
                    error = validate(xml)
                }
                if (error) {
                    invalid << [uid: it.uid, reason: error]
                } else {
                    validCount++
                }
            }
            def result = "${validCount} are valid\n"
            invalid.each {
                result << "${it.uid} is not valid: ${it.reason}\n"
            }
            render result
        }
    }

    /************* Data Hub services *********/
    def institutionsForDataHub = {
        def list = params.pg.listMemberInstitutions()
        renderAsJson list, null, list.toString().encodeAsMD5()
    }

    def collectionsForDataHub = {
        def list = params.pg.listMemberCollections()
        renderAsJson list, null, list.toString().encodeAsMD5()
    }


    /************* Contact services **********/

    static final String CONTACT_HEADER = "title, first name, last name, email, phone, fax, mobile, publish, dateCreated, lastUpdated\n"

    /**
     * Returns a single contact (independent of any entity).
     * URI form: /contacts/{id}
     * @param id the database id of the contact
     */
    def contacts = {
        if (params.id) {
            def c = Contact.get(params.id)
            if (c) {
                addContentLocation "/ws/contacts/${params.id}"
                addVaryAcceptHeader()
                def cm = buildContactModel(c)
                withFormat {
                    csv {render (contentType: 'text/csv', text: CONTACT_HEADER + mapToCsv(cm))}
                    xml {render (contentType: 'text/xml', text: objToXml(cm, 'contact'))}
                    json {render cm as JSON}
                }
            } else {
                badRequest ' no such id'
            }
        } else {
            addContentLocation "/ws/contacts"
            addVaryAcceptHeader()
            withFormat {
                csv {render (contentType: 'text/csv',
                        text: CONTACT_HEADER + Contact.list().collect { mapToCsv(buildContactModel(it)) }.join(''))}
                xml {render (contentType: 'text/xml', text: objToXml(Contact.list().collect { buildContactModel(it) }, 'contacts'))}
                json {render Contact.list().collect { buildContactModel(it) } as JSON}
            }
        }
    }

    def buildContactModel(contact) {
        return new LinkedHashMap(
            [title: contact.title, firstName: contact.firstName, lastName: contact.lastName, email: contact.email, phone: contact.phone,
             fax: contact.fax, mobile: contact.mobile, publish: contact.publish, dateCreated: contact.dateCreated, lastUpdated: contact.lastUpdated])
    }

    def buildContactForModel(cf, urlContext) {
        return new LinkedHashMap(
            [contact: buildContactModel(cf.contact), role: cf.role, primaryContact: cf.primaryContact,
                    editor: cf.administrator, notify: cf.notify, dateCreated: cf.dateCreated, lastUpdated: cf.dateLastModified,
                    uri: "${ConfigurationHolder.config.grails.serverURL}/ws/${urlContext}/${cf.entityUid}/contacts/${cf.id}"])
    }

    /**
     * Returns a contact that matches the supplied email.
     */
    def getContactByEmail = {
        def email = params.email
        if (email) {
            def c = Contact.findByEmail(email)
            if (c) {
                addContentLocation "/ws/contacts/${c.id}"
                addVaryAcceptHeader()
                def cm = buildContactModel(c)
                cm.id = c.id
                render cm as JSON
            } else {
                notFound 'no such email'
            }
        } else {
            badRequest 'no email provided'
        }
    }

    /************* contact update services **********/
    def updateContact = {
        def props = params.json
        props.userLastModified = session.username
        //println "body = "  + props
        if (params.id) {
            // update
            def c = Contact.get(params.id)
            if (c) {
                bindData(c, props as Map, ['id'])
                c.save(flush: true)
                c.errors.each { println it}
                addContentLocation "/ws/contacts/${c.id}"
                def cm = buildContactModel(c)
                cm.id = c.id
                render cm as JSON
            } else {
                badRequest 'no such id'
            }
        } else {
            // create
            if (props.email) {
                def c = new Contact(props as Map)
                c.save(flush: true)
                c.errors.each { println it}
                addContentLocation "/ws/contacts/${c.id}"
                def cm = buildContactModel(c)
                cm.id = c.id
                render cm as JSON
                //created 'contacts', c.id
            } else {
                badRequest 'email must be supplied'
            }
        }
    }

    def deleteContact = {
        def props = params.json
        //println "body = "  + props
        if (params.id) {
            // update
            def c = Contact.get(params.id)
            if (c) {
                // remove its links as well
                ActivityLog.log session.username as String, true, Action.DELETE, "contact ${c.buildName()}"
                // need to delete any ContactFor links first
                ContactFor.findAllByContact(c).each {
                    it.delete(flush: true)
                }
                c.delete(flush: true)
                success "deleted"
            } else {
                badRequest 'contact does not exist'
            }
        } else {
            badRequest 'no id supplied'
        }
    }

    /**
     * Returns all contacts for a single entity.
     * URI form: /{entity}/{uid}/contacts
     * @param entity an entity type in url form ie one of collection, institution, dataProvider, dataResource, dataHub
     * @param uid the entity instance
     */
    def contactsForEntity = {
        def contactList = params.pg.getContacts().collect { buildContactForModel(it, params.pg.urlForm()) }
        addContentLocation "/ws/${params.entity}/${params.pg.uid}/contacts"
        addVaryAcceptHeader()
        withFormat {
            csv {
                def out = new StringWriter()
                out << "name, role, primary contact, editor, notify, email, phone\n"
                contactList.each {
                    out << "\"${it.name}\",\"${it.role}\",${it.primaryContact},${it.editor},${it.notify},${it.email?:""},${it.phone?:""}\n"
                }
                response.addHeader "Content-Type", "text/csv"
                render out.toString()
            }
            xml {render (contentType: 'text/xml', text: objToXml(contactList, 'contactFors'))}
            json {render contactList as JSON}
        }
    }

    /**
     * Returns a single contact for a single entity.
     * URI form: /{entity}/{uid}/contacts/{id}
     * @param entity an entity type in url form ie one of collection, institution, dataProvider, dataResource, dataHub
     * @param uid the entity instance
     * @param id the database id of the contact relationship (contactFor)
     */
    def contactForEntity = {
        if (params.id) {
            def cm = buildContactForModel(ContactFor.get(params.id as Long), params.pg.urlForm())
            addContentLocation "/ws/${params.entity}/${params.pg.uid}/contacts/${params.id}"
            addVaryAcceptHeader()
            withFormat {
                csv {
                    def out = new StringWriter()
                    out << "title, first name, last name, role, primary contact, editor, notify, email, phone, fax, mobile\n"
                    out << "\"${cm.contact.title?:""}\",\"${cm.contact.firstName?:""}\",\"${cm.contact.lastName?:""}\",\"${cm.role}\",${cm.primaryContact},${cm.editor},${cm.notify},${cm.contact.email?:""},${cm.contact.phone?:""},${cm.contact.fax?:""},${cm.contact.mobile?:""}\n"
                    render (contentType: 'text/csv', text: out.toString())
                }
                xml {render (contentType: 'text/xml', text: objToXml(cm, 'contactFor'))}
                json {render cm as JSON}
            }
        } else {
            forward(action:'contactsForEntity')
        }
    }

    static final String SHORT_CONTACTS_HEADER = "entity name, entity UID, entity acronym, contact name, contact email, contact phone\n"

    /**
     * Returns all contacts for all entities of the specified type.
     * URI form: /{entity}/contacts
     * @param entity an entity type in url form ie one of collection, institution, dataProvider, dataResource, dataHub
     */
    def contactsForEntities = {
        def domain = grailsApplication.getClassForName("au.org.ala.collectory.${capitalise(params.entity)}")
        def model = buildContactsModel(domain.list([sort:'name']))
        addContentLocation "/ws/${params.entity}/contacts"
        addVaryAcceptHeader()
        withFormat {
            csv {
                render (contentType: 'text/csv',
                        text: SHORT_CONTACTS_HEADER + listToCsv(model))
            }
            xml {render (contentType: 'text/xml', text: objToXml(model, 'contacts'))}
            json {
                render model as JSON
            }
        }
    }

    def static buildContactsModel(list) {
        return list.collect {
            def map = [:]
            map.entityName = it.name
            map.entityUid = it.uid
            map.entityAcronym = it.acronym ?: ""
            map.contactName = it.primaryContact?.contact?.buildName() ?: ""
            map.contactEmail = it.primaryContact?.contact?.email ?: ""
            map.contactPhone = it.primaryContact?.contact?.phone ?: ""
            map.uri = it.primaryContact ? "${ConfigurationHolder.config.grails.serverURL}/ws/${ProviderGroup.urlFormFromUid(it.uid)}/${it.uid}/contacts/${it.primaryContact?.id}" : ''

            return map
        }
    }

    /**
     * Returns a list of entities that the specified contact is authorised to edit.
     * @param email of the user
     */
    def authorisedForContact = {
        def contact = Contact.get(params.id)
        if (!contact) {
            badRequest "contact ${params.id} does not exist"
        }
        else {
            def result = authService.authorisedForUser(contact)
            renderAsJson result.sorted, result.latestMod, result.keys.toString().encodeAsMD5()
        }
    }

    /**
     * Returns a json list of contacts to be notified on significant entity events.
     * @param uid of the entity
     */
    def notifyList = {
        if (params.uid) {
            def contactFors = ContactFor.findAllByEntityUidAndNotify(params.uid, true).collect {
                buildContactForModel(it, ProviderGroup.urlFormFromUid(params.uid))
            }
            render contactFors as JSON
        } else {
            badRequest "must specify a uid"
        }
    }

    /**
     * Write-only service that accepts notification payloads.
     *
     * Example payload:
     * { event: 'user annotation', id: 'ann03468', uid: 'co13' }
     */
    def notification() {
        //println "notify"
        if (request.method != 'POST') {
            println "not allowed"
            notAllowed()
        } else {
            //println params.json
            def payload = params.json
            def uid = payload.uid
            def event = payload.event
            def id = payload.id
            def action = payload.action
            if (!(uid && event && id && action)) {
                println "bad request"
                badRequest 'must specify a uid, an event and an event id'
            } else {
                //println "OK"
                // register the event
                ActivityLog.log([user: 'notify-service', isAdmin: false, action: "${action}d ${id}", entityUid: uid])
                success "notification accepted"
            }
        }
    }

    /************* contactFor update services **********/
    /**
     * Updates or creates a contact association for a single entity.
     * URI form: /ws/$entity/$uid/contacts/$id?
     * @param entity an entity type in url form ie one of collection, institution, dataProvider, dataResource, dataHub
     * @param uid the entity instance
     * @param id the contact id
     */
    def updateContactFor = {
        def props = params.json
        props.userLastModified = session.username
        //println "body = "  + props
        def c = Contact.get(params.id)
        def cf = ContactFor.findByContactAndEntityUid(c, params.pg.uid)
        if (cf) {
            // update
            bindData(cf, props as Map, ['entityUid'])
            c.save(flush: true)
            c.errors.each { println it}
            success 'updated'
        } else {
            // create
            if (c) {
                params.pg.addToContacts c,
                        props.role ?: '',
                        (props.administrator ?: false) as Boolean,
                        (props.primaryContact ?: false) as Boolean,
                        props.userLastModified
                created 'contactFor', params.pg.uid
            } else {
                badRequest "contact doesn't exist"
            }
        }
    }

    def deleteContactFor = {
        def props = params.json
        props.userLastModified = session.username
        println "body = "  + props
        def c = Contact.get(params.id)
        def cf = ContactFor.findByContactAndEntityUid(c, params.pg.uid)
        if (cf) {
            cf.delete(flush: true)
            success "deleted"
        } else {
            badRequest 'contact association does not exist'
        }
    }

    /************* connection parameters services **********/

    def connectionParameters = {
        ProviderGroup pg = params.pg
        if (pg.entityType() != DataResource.ENTITY_TYPE) {
            badRequest "must be a data resource"
        } else {
            response.setContentType("application/json")
            render metadataService.convertAnyLocalPaths(pg.connectionParameters)
        }
    }

    /**** html fragment services ****/
    def getFragment = {
        def pg = ProviderGroup._get(params.uid)
        if (!pg) {
            def message = "${message(code: 'default.not.found.message', args: [message(code: 'entity.label', default: 'Entity'), params.uid])}"
            render message
        } else {
            render(view: "${params.entity}Fragment", model: [instance: pg])
        }
    }

    /** temporary dump of map of coll code/inst code pairs with mapped collection and institution data **/
    def codeMapDump = {
        def csv = "collectionCode,institutionCode,collectionUid,collectionName,institutionUid,institutionName," +
                "dataProviderUid,dataProviderName,dataHubUid,dataHubName,taxonomicHints\n"
        ProviderMap.list().each {
            def collectionCodes = it.collectionCodes
            def institutionCodes = it.institutionCodes
            // write record for each combo
            collectionCodes.each { coll ->
                institutionCodes.each { inst ->
                    csv += coll.code + "," +
                            inst.code + "," +
                            it.collection.uid + "," +
                            "\"" + it.collection.name + "\"," +
                            it.collection.institution?.uid + "," +
                            "\"" + it.collection.institution?.name + "\"," +
                            "dp20,OZCAM (Online Zoological Collections of Australian Museums) Provider," +
                            "dh1,Online Zoological Collections of Australian Museums," +
                            encodeHints(it.collection.listTaxonomyHints()) + "\n"
                }
            }
        }
        render(contentType: 'text/csv', text:csv)
    }

    private String encodeHints(hints) {
        def result = hints.collect {
            it.rank + ":" + it.name
        }
        return result.join(';')
    }

    /**
     * Converts a map or list to 'tight' xml string, ie keys become element names <keyName> rather than <entry key='keyName'>..
     * @param obj the map or list to represent
     * @param root the container element
     * @return XML string
     */
    def objToXml(obj, root) {
        def writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml."${root}" {
            toXml(obj,xml, (root[-1] == 's') ? root[0..-2] : 'item')
        }
        return writer.toString()
    }

    /* called recursively to build xml */
    def toXml(obj, xml, listElement) {
        if (obj instanceof List) {
            obj.each { item ->
                xml."${listElement}" { toXml(item, xml, listElement) }
            }
        } else {
            obj.each { key, value ->
                if (value && value instanceof Map) {
                    xml."${key}" {toXml(value, xml, listElement)}
                } else {
                    xml."${key}"(value)
                }
            }
        }
    }

    def listToCsv(list) {
        def out = new StringWriter()
        list.each {
            out << mapToCsv(it)
        }
        return out.toString()
    }

    /**
     * Converts a map to a csv row
     * @param map the map to represent
     * @return csv string
     */
    def mapToCsv(map) {
        def out = new StringWriter()
        def list = map.collect {key, value -> value}
        list.eachWithIndex {it, idx ->
            out << toCsvItem(it)
            if (idx == list.size() - 1) {
                out << '\n'
            } else {
                out << ','
            }
        }
        return out.toString()
    }

    String toCsvItem(item) {
        if (!item) return ""
        return '"' + item + '"'
    }

}

class ContactForEntity {
    String entityName
    String entityUid
    String entityAcronym
    String contactName
    String contactEmail
    String contactPhone
    String uri
}
