package au.org.ala.collectory

import grails.converters.JSON
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.tools.zip.ZipFile
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.multipart.MultipartFile
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogListener

/**
 * This is a base class for all provider group entities types.
 *
 * It provides common code for shared attributes like contacts.
 */
abstract class ProviderGroupController {

    static String entityName = "ProviderGroup"
    static String entityNameLower = "providerGroup"

    def idGeneratorService, collectoryAuthService, metadataService, gbifService, dataImportService

/*
 * Access control
 *
 * All methods require EDITOR role.
 * Edit methods require ADMIN or the user to be an administrator for the entity.
 */
    def beforeInterceptor = [action:this.&auth]

    def auth() {
        if (!collectoryAuthService?.userInRole(ProviderGroup.ROLE_EDITOR) && !grailsApplication.config.security.cas.bypass.toBoolean()) {
            response.setHeader("Content-type", "text/plain; charset=UTF-8")
            render message(code: "provider.group.controller.01", default: "You are not authorised to access this page. You do not have 'Collection editor' rights.")
            return false
        }
    }
    // helpers for subclasses
    protected username = {
        collectoryAuthService?.username() ?: 'unavailable'
    }

    protected isAdmin = {
        collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN) ?: false
    }
/*
 End access control
 */

    /**
     * List providers for institutions/collections
     */
    def showProviders = {
        def provs = DataLink.findAllByConsumer(params.id).collect {it.provider}
        render provs as JSON
    }

    /**
     * List consumers of data resources/providers
     */
    def showConsumers = {
        def cons = DataLink.findAllByProvider(params.id).collect {it.consumer}
        render cons as JSON
    }

    /**
     * Checks for optimistic lock failure
     *
     * @param pg the entity being updated
     * @param view the view to return to if lock fails
     */
    def checkLocking = { pg, view ->
        if (params.version) {
            def version = params.version.toLong()
            if (pg.version > version) {
                println "db version = ${pg.version} submitted version = ${version}"
                pg.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: "${pg.urlForm()}.label", default: pg.entityType())] as Object[],
                        message(code: "provider.group.controller.02", default: "Another user has updated this") + " ${pg.entityType()} " + message(code: "provider.group.controller.03", default: "while you were editing. This page has been refreshed with the current values."))
                println "error added - rendering ${view}"
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(view: view, model: [command: pg])
            }
            return pg.version > version
        }
    }

    /**
     * Edit base attributes.
     * @param id
     */
    def edit = {
        def pg = get(params.id)
        if (!pg) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "list")
        } else {
            // are they allowed to edit
            if (isAuthorisedToEdit(pg.uid)) {
                params.page = params.page ?: '/shared/base'
                render(view:params.page, model:[command: pg, target: params.target])
            } else {
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
            }
        }
    }

    def editAttributions = {
        def pg = get(params.id)
        if (!pg) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "list")
        } else {
            // are they allowed to edit
            if (isAuthorisedToEdit(pg.uid)) {
                render(view: '/shared/attributions', model:[BCI: pg.hasAttribution('at1'), CHAH: pg.hasAttribution('at2'),
                        CHACM: pg.hasAttribution('at3'), command: pg])
            } else {
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
            }
        }
    }

    /**
     * Create a new entity instance.
     *
     */
    def create = {
        println("\r\n" + entityName + "\r\n")

        def name = params.name ?: message(code: "provider.group.controller.05", default: "enter name")
        //def name = message(code: 'provider.group.controller.05', default: 'enter name')
        //def name = 'enter name'
        ProviderGroup pg
        switch (entityName) {
            case Collection.ENTITY_TYPE:
                pg = new Collection(uid: idGeneratorService.getNextCollectionId(), name: name, userLastModified: collectoryAuthService?.username())
                if (params.institutionUid && Institution.findByUid(params.institutionUid)) {
                    pg.institution = Institution.findByUid(params.institutionUid)
                }
                break
            case Institution.ENTITY_TYPE:
                pg = new Institution(uid: idGeneratorService.getNextInstitutionId(), name: name, userLastModified: collectoryAuthService?.username()); break
            case DataProvider.ENTITY_TYPE:
                pg = new DataProvider(uid: idGeneratorService.getNextDataProviderId(), name: name, userLastModified: collectoryAuthService?.username()); break
            case DataResource.ENTITY_TYPE:
                pg = new DataResource(uid: idGeneratorService.getNextDataResourceId(), name: name, userLastModified: collectoryAuthService?.username())
                if (params.dataProviderUid && DataProvider.findByUid(params.dataProviderUid)) {
                    pg.dataProvider = DataProvider.findByUid(params.dataProviderUid)
                }
                break
            case DataHub.ENTITY_TYPE:
                pg = new DataHub(uid: idGeneratorService.getNextDataHubId(), name: name, userLastModified: collectoryAuthService?.username()); break
        }

        if (!pg.hasErrors() && pg.save(flush: true)) {
            // add the user as a contact if specified
            if (params.addUserAsContact) {
                addUserAsContact(pg, params)
            }
            flash.message = "${message(code: 'default.created.message', args: [message(code: "${pg.urlForm()}", default: pg.urlForm()), pg.uid])}"
            redirect(action: "show", id: pg.uid)
        } else {
            flash.message = message(code: "provider.group.controller.06", default: "Failed to create new") + " ${entityName}"
            redirect(controller: 'admin', action: 'index')
        }
    }

    /**
     * Adds the current user as a contact for the specified entity.
     *
     * Used when creating new entities.
     * @param pg the entity
     * @param params values for contact fields if the contact does not already exist
     */
    void addUserAsContact(ProviderGroup pg, params) {
        def user = collectoryAuthService?.username()
        // find contact
        Contact c = Contact.findByEmail(user)
        if (!c) {
            // create from params
            c = new Contact(email: user,
                userLastModified: user,
                firstName: params.firstName ?: null,
                lastName: params.lastName ?: null,
                title: params.title ?: null,
                phone: params.phone ?: null,
                publish: (params.publish == 'true')
            )
            c.save(flush:true)
            if (c.hasErrors()) {
                c.errors.each {
                    log.debug("Error saving new contact for ${user} - ${it}")
                    println "Error saving new contact for ${user} - ${it}"
                }
            }
        }
        pg.addToContacts(c, params.role ?: 'editor', true, false, user)
    }

    def cancel = {
        //println "Cancel - returnTo = ${params.returnTo}"
        if (params.returnTo) {
            redirect(uri: params.returnTo)
        } else {
            redirect(action: "show", id: params.uid ?: params.id)
        }
    }

    /**
     * This does generic updating from a form. Works for all properties that can be bound by default.
     */
    def genericUpdate = { pg, view ->
        if (pg) {
            if (checkLocking(pg,view)) { return }

            pg.properties = params
            pg.userLastModified = collectoryAuthService?.username()
            if (!pg.hasErrors() && pg.save(flush: true)) {
                flash.message =
                  "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                redirect(action: "show", id: pg.uid)
            }
            else {
                render(view: view, model: [command: pg])
            }
        } else {
            flash.message =
                "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    /**
     * Update base attributes
     */
    def updateBase = { //BaseCommand cmd ->
        BaseCommand cmd = new BaseCommand()

        bindData(cmd, params)
        //Institution institution
//        def cmd = new BaseCommand(params)

        if(cmd.hasErrors()) {
            cmd.errors.each {println it}
            cmd.id = params.id as int   // these do not seem to be injected
            cmd.version = params.version as int
            render(view:'/shared/base', model: [command: cmd])
        } else {
            def pg = get(params.id)
            if (pg) {
                if (checkLocking(pg,'/shared/base')) { return }

                // special handling for membership
                pg.networkMembership = toJson(params.networkMembership)
                params.remove('networkMembership')

                pg.properties = params
                pg.userLastModified = collectoryAuthService?.username()
                if (!pg.hasErrors() && pg.save(flush: true)) {
                    flash.message =
                        "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                    redirect(action: "show", id: pg.uid)
                }
                else {
                    render(view: "/shared/base", model: [command: pg])
                }
            } else {
                flash.message =
                    "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
    }

    /**
     * Update descriptive attributes
     */
    def updateDescription = {
        def pg = get(params.id)
        if (pg) {
            if (checkLocking(pg,'description')) { return }

            // do any entity specific processing
            entitySpecificDescriptionProcessing(pg, params)

            pg.properties = params
            pg.userLastModified = collectoryAuthService?.username()
            if (!pg.hasErrors() && pg.save(flush: true)) {
                flash.message =
                  "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                redirect(action: "show", id: pg.uid)
            }
            else {
                render(view: "description", model: [command: pg])
            }
        } else {
            flash.message =
                "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    def entitySpecificDescriptionProcessing(pg, params) {
        // default is to do nothing
        // sub-classes override to do specific processing
    }
    
    /**
     * Update location attributes
     */
    def updateLocation = {

        LocationCommand cmd = new LocationCommand()
        bindData(cmd, params)

        if(cmd.hasErrors()) {
            cmd.id = params.id as int   // these do not seem to be injected
            cmd.version = params.version as int
            render(view:'/shared/location', model: [command: cmd])
        } else {
            def pg = get(params.id)
            if (pg) {
                if (checkLocking(pg,'/shared/location')) { return }

                // special handling for lat & long
                if (!params.latitude) { params.latitude = -1 }
                if (!params.longitude) { params.longitude = -1 }

                // special handling for embedded address - need to create address obj if none exists and we have data
                if (!pg.address && [params.address?.street, params.address?.postBox, params.address?.city,
                    params.address?.state, params.address?.postcode, params.address?.country].join('').size() > 0) {
                    pg.address = new Address()
                }

                // special handling for embedded postal address - need to create address obj if none exists and we have data
                /*if (!pg.postalAddress && [params.postalAddress?.street, params.postalAddress?.postBox, params.postalAddress?.city,
                    params.postalAddress?.state, params.postalAddress?.postcode, params.postalAddress?.country].join('').size() > 0) {
                    pg.postalAddress = new Address()
                }*/

                pg.properties = params
                pg.userLastModified = collectoryAuthService?.username()
                if (!pg.hasErrors() && pg.save(flush: true)) {
                    flash.message =
                      "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                    redirect(action: "show", id: pg.uid)
                } else {
                    render(view: "/shared/location", model: [command: pg])
                }
            } else {
                flash.message =
                    "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
    }

    def updateTaxonomyHints = {
        def pg = get(params.id)
        if (pg) {
            if (checkLocking(pg,'/shared/editTaxonomyHints')) { return }

            // handle taxonomy hints
            def ranks = params.findAll { key, value ->
                key.startsWith('rank_') && value
            }
            def hints = ranks.sort().collect { key, value ->
                def idx = key.substring(5)
                def name = params."name_${idx}"
                return ["${value}": name]
            }
            def th = pg.taxonomyHints ? JSON.parse(pg.taxonomyHints) : [:]
            th.coverage = hints
            pg.taxonomyHints = th as JSON

            pg.userLastModified = collectoryAuthService?.username()
            if (!pg.hasErrors() && pg.save(flush: true)) {
                flash.message =
                  "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                redirect(action: "show", id: pg.uid)
            }
            else {
                render(view: "/shared/editTaxonomyHints", model: [command: pg])
            }
        } else {
            flash.message =
                "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    def updateTaxonomicRange = {
        def pg = get(params.id)
        if (pg) {
            if (checkLocking(pg,'/shared/taxonomicRange')) { return }

            // handle taxonomic range
            def rangeList = params.range.tokenize(',')
            def th = pg.taxonomyHints ? JSON.parse(pg.taxonomyHints) : [:]
            th.range = rangeList
            pg.taxonomyHints = th as JSON
            println pg.taxonomyHints

            pg.userLastModified = collectoryAuthService?.username()
            if (!pg.hasErrors() && pg.save(flush: true)) {
                flash.message =
                  "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                redirect(action: "show", id: pg.uid)
            }
            else {
                render(view: "/shared/taxonomicRange", model: [command: pg])
            }
        } else {
            flash.message =
                "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    def updateContactRole = {
        def contactFor = ContactFor.get(params.contactForId)
        if (contactFor) {
            contactFor.properties = params
            contactFor.userLastModified = collectoryAuthService?.username()
            if (!contactFor.hasErrors() && contactFor.save(flush: true)) {
                flash.message = "${message(code: 'contactRole.updated.message')}"
                redirect(action: "edit", id: params.id, params: [page: '/shared/showContacts'])
            } else {
                render(view: '/shared/contactRole', model: [command: ProviderGroup._get(params.id), cf: contactFor])
            }

        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contactFor.label', default: "Contact for ${entityNameLower}"), params.contactForId])}"
            redirect(action: "show", id: params.id)
        }
    }

    def addContact = {
        def pg = get(params.id)
        if (!pg) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "list")
        } else {
            if (isAuthorisedToEdit(pg.uid)) {
                Contact contact = Contact.get(params.addContact)
                if (contact) {
                    pg.addToContacts(contact, "editor", true, false, collectoryAuthService?.username())
                    redirect(action: "edit", params: [page:"/shared/showContacts"], id: params.id)
                }
            } else {
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
            }
        }
    }

    def addNewContact = {
        def pg = get(params.id)
        def contact = Contact.get(params.contactId)
        if (contact && pg) {
            // add the contact to the collection
            pg.addToContacts(contact, "editor", true, false, collectoryAuthService?.username())
            redirect(action: "edit", params: [page:"/shared/showContacts"], id: pg.uid)
        } else {
            if (!pg) {
                flash.message = message(code: "provider.group.controller.07", default: "Contact was created but") + " ${entityNameLower} " + message(code: "provider.group.controller.08", default: "could not be found. Please edit") + " ${entityNameLower} " + message(code: "provider.group.controller.09", default: "and add contact from existing.")
                redirect(action: "list")
            } else {
                if (isAuthorisedToEdit(pg.uid)) {
                    // contact must be null
                    flash.message = message(code: "provider.group.controller.10", default: "Contact was created but could not be added to the") + " ${pg.urlForm()}. " + message(code: "provider.group.controller.11", default: "Please add contact from existing.")
                    redirect(action: "edit", params: [page:"/shared/showContacts"], id: pg.uid)
                } else {
                    response.setHeader("Content-type", "text/plain; charset=UTF-8")
                    render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
                }
            }
        }
    }

    def removeContact = {
        def pg = get(params.id)
        if (!pg) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "list")
        } else {
            // are they allowed to edit
            if (isAuthorisedToEdit(pg.uid)) {
                ContactFor cf = ContactFor.get(params.idToRemove)
                if (cf) {
                    cf.delete()
                    redirect(action: "edit", params: [page:"/shared/showContacts"], id: params.id)
                }
            } else {
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
            }
        }
    }

    def editRole = {
        def contactFor = ContactFor.get(params.id)
        if (!contactFor) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contactFor.label', default: "Contact for ${entityNameLower}"), params.id])}"
            redirect(action: "list")
        } else {
            ProviderGroup pg = ProviderGroup._get(contactFor.entityUid)
            if (pg) {
                // are they allowed to edit
                if (isAuthorisedToEdit(pg.uid)) {
                    render(view: '/shared/contactRole', model: [command: pg, cf: contactFor, returnTo: params.returnTo])
                } else {
                    response.setHeader("Content-type", "text/plain; charset=UTF-8")
                    render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
                }
            } else {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contactFor.entityUid.label', default: "Entity for ${entityNameLower}"), params.id])}"
                redirect(action: "list")
            }
        }
    }
    /**
     * Displays the form for loading a single pre-downloaded resource from GBIF. It assumes that there is a single resource
     * in the file.
     */
    def gbifUpload = {
    }

    /**
     * Uploads the supplied GBIF file creating a new data resource based on the supplied EML details
     */
    def downloadGBIFFile = {

        log.info("Downloading file: " + params.url)

        try {
            def dr = gbifService.createGBIFResourceFromArchiveURL(params.url)
            if(dr){
                render(text:([success:true, dataResourceName:dr.name, dataResourceUid: dr.uid] as JSON).toString(),
                        encoding:"UTF-8", contentType: "application/json")
            } else {
                render(text:([success:false] as JSON).toString(), encoding:"UTF-8", contentType: "application/json")
            }

        } catch (Exception e){
            log.error(e.getMessage(), e)
            render(text:([success:false] as JSON).toString(), encoding:"UTF-8", contentType: "application/json")
        }
    }

    def upload = {
        def pg = get(params.id)
        if (!pg) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "upload")
        } else {
            // are they allowed to edit
            if (isAuthorisedToEdit(pg.uid)) {
                render(view:'upload', model:[
                        instance: pg,
                        connectionProfiles: metadataService.getConnectionProfilesWithFileUpload(),
                        connectionParams: metadataService.getConnectionParameters()
                ])
            } else {
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
            }
        }
    }

    def uploadDataFile = {

        //get the UID
        def dataResource = get(params.id)

        def f = request.getFile('myFile')
        if (f.empty) {
            flash.message = message(code: "provider.group.controller.12", default: "file cannot be empty")
            response.setHeader("Content-type", "text/plain; charset=UTF-8")
            render(view: 'upload')
            return
        }

        dataImportService.importDataFileForDataResource(dataResource, f, params)
        redirect([controller: 'dataResource', action: 'show', id: dataResource.uid])
    }



    /**
     * Get the instance for this entity based on either uid or DB id.
     * All sub-classes must implement this method.
     *
     * @param id UID or DB id
     * @return the entity of null if not found
     */
    abstract protected ProviderGroup get(id)

    /**
     * Update images
     */
    def updateImages = {
        def pg = get(params.id)
        def target = params.target ?: "imageRef"
        if (pg) {
            if (checkLocking(pg,'/shared/images')) { return }

            // special handling for uploading image
            // we need to account for:
            //  a) upload of new image
            //  b) change of metadata for existing image
            // removing an image is handled separately
            MultipartFile file
            switch (target) {
                case 'imageRef': file = params.imageFile; break
                case 'logoRef': file = params.logoFile; break
            }
            if (file?.size) {  // will only have size if a file was selected
                // save the chosen file
                if (file.size < 200000) {   // limit file to 200Kb
                    def filename = file.getOriginalFilename()
                    log.debug "filename=${filename}"

                    // update filename
                    pg."${target}" = new Image(file: filename)
                    String subDir = pg.urlForm()

                    def colDir = new File(ConfigurationHolder.config.repository.location.images as String, subDir)
                    colDir.mkdirs()
                    File f = new File(colDir, filename)
                    log.debug "saving ${filename} to ${f.absoluteFile}"
                    file.transferTo(f)
                    //ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.UPLOAD_IMAGE, filename
                } else {
                    println "reject file of size ${file.size}"
                    pg.errors.rejectValue('imageRef', 'image.too.big', message(code: "provider.group.controller.13", default: "The image you selected is too large. Images are limited to 200KB."))
                    response.setHeader("Content-type", "text/plain; charset=UTF-8")
                    render(view: "/shared/images", model: [command: pg, target: target])
                    return
                }
            }
            pg.properties = params
            pg.userLastModified = collectoryAuthService?.username()
            if (!pg.hasErrors() && pg.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                redirect(action: "show", id: pg.uid)
            } else {
                render(view: "/shared/images", model: [command: pg, target: target])
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityName), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    def removeImage = {
        def pg = get(params.id)
        if (pg) {
            if (isAuthorisedToEdit(pg.uid)) {
                if (checkLocking(pg,'/shared/images')) { return }

                if (params.target == 'logoRef') {
                    pg.logoRef = null
                } else {
                    pg.imageRef = null
                }
                pg.userLastModified = collectoryAuthService?.username()
                if (!pg.hasErrors() && pg.save(flush: true)) {
                    flash.message = "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                    redirect(action: "show", id: pg.uid)
                } else {
                    render(view: "/shared/images", model: [command: pg])
                }
            } else {
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    def updateAttributions = {
        def pg = get(params.id)
        if (pg) {
            if (checkLocking(pg,'/shared/attributions')) { return }

            if (params.BCI && !pg.hasAttribution('at1')) {
                pg.addAttribution 'at1'
            }
            if (!params.BCI && pg.hasAttribution('at1')) {
                pg.removeAttribution 'at1'
            }
            if (params.CHAH && !pg.hasAttribution('at2')) {
                pg.addAttribution 'at2'
            }
            if (!params.CHAH && pg.hasAttribution('at2')) {
                pg.removeAttribution 'at2'
            }
            if (params.CHACM && !pg.hasAttribution('at3')) {
                pg.addAttribution 'at3'
            }
            if (!params.CHACM && pg.hasAttribution('at3')) {
                pg.removeAttribution 'at3'
            }

            if (pg.isDirty()) {
                pg.userLastModified = collectoryAuthService?.username()
                if (!pg.hasErrors() && pg.save(flush: true)) {
                    flash.message =
                      "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
                    redirect(action: "show", id: pg.uid)
                }
                else {
                    render(view: "description", model: [command: pg])
                }
            } else {
                redirect(action: "show", id: pg.uid)
            }
        } else {
            flash.message =
                "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    def delete = {
        def pg = get(params.id)
        if (pg) {
            if (collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN) || grailsApplication.config.security.cas.bypass.toBoolean()) {
                def name = pg.name
                log.info ">>${collectoryAuthService?.username()} deleting ${entityName} " + name
                //ActivityLog.log collectoryAuthService?.username(), authService?.userInRole(ProviderGroup.ROLE_ADMIN), pg.uid, Action.DELETE
                try {
                    // remove contact links (does not remove the contact)
                    ContactFor.findAllByEntityUid(pg.uid).each {
                        log.info "Removing link to contact " + it.contact?.buildName()
                        it.delete()
                    }
                    // delete
                    pg.delete(flush: true)
                    flash.message = "${message(code: 'default.deleted.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), name])}"
                    redirect(action: "list")
                } catch (org.springframework.dao.DataIntegrityViolationException e) {
                    flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), name])}"
                    redirect(action: "show", id: params.id)
                }
            } else {
                response.setHeader("Content-type", "text/plain; charset=UTF-8")
                render(message(code: "provider.group.controller.04", default: "You are not authorised to access this page."))
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "list")
        }
    }

    def showChanges = {
        def instance = get(params.id)
        if (instance) {
            // get audit records
            def changes = AuditLogEvent.findAllByUri(instance.uid,[sort:'lastUpdated',order:'desc'])
            render(view:'/shared/showChanges', model:[changes:changes, instance:instance])
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "list")
        }
    }

    def getChanges(uid) {
        // get audit records
        return AuditLogEvent.findAllByUri(uid,[sort:'lastUpdated',order:'desc',max:10])
    }

    protected String toJson(param) {
        if (!param) {
            return ""
        }
        if (param instanceof String) {
            // single value
            return ([param] as JSON).toString()
        }
        def list = param.collect {
            it.toString()
        }
        return (list as JSON).toString()
    }

    protected String toSpaceSeparatedList(param) {
        if (!param) {
            return ""
        }
        if (param instanceof String) {
            // single value
            return param
        }
        return param.join(' ')
    }

    def auditLog(ProviderGroup pg, String eventName, String property, String oldValue, String newValue, Object persistedObject) {
        def audit = new AuditLogEvent(
                  actor: username(),
                  uri: pg.uid,   /* MEW repurposing of uri */
                  className: pg.getClass().name,
                  eventName: eventName,
                  persistedObjectId: persistedObject.id?.toString(),
                  persistedObjectVersion: persistedObject.version,
                  propertyName: property,
                  oldValue: truncate(oldValue),
                  newValue: truncate(newValue)
          )
        audit.save()
    }

    private String truncate(str) {
        return (str?.length() > AuditLogListener.TRUNCATE_LENGTH) ? str?.substring(0, AuditLogListener.TRUNCATE_LENGTH) : str
    }

    protected boolean isAuthorisedToEdit(uid) {
        if (grailsApplication.config.security.cas.bypass.toBoolean() || isAdmin()) {
            return true
        } else {
            def email = RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.email
            if (email) {
                return ProviderGroup._get(uid)?.isAuthorised(email)
            }
        }
        return false
    }
}
