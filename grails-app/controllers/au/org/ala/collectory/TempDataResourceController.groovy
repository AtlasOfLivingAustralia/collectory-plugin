/*
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

package au.org.ala.collectory

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

class TempDataResourceController {

    def crudService, authService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    /** make sure that uid params point to an existing entity and json is parsable **/
    def beforeInterceptor = this.&check

    def check() {
        def uid = params.uid
        if (uid) {
            // it must exist
            def drt = TempDataResource.findByUid(uid)
            if (drt) {
                params.drt = drt
            } else {
                // doesn't exist
                notFound "no entity with uid = ${uid}"
                return false
            }
        }
        // check payload
        if (request.getContentLength() == 0) {
            // no payload so return OK as entity exists (if specified)
            success "no post body"
            return false
        }
        try {
            params.json = request.JSON
        } catch (Exception e) {
            println "exception caught ${e}"
            if (request.getContentLength() > 0) {
                badRequest 'cannot parse request body as JSON'
                return false
            }
        }
    }

    def unauthorised = {
        // using the 'forbidden' response code here as 401 causes the client to ask for a log in
        render(status:403, text: 'You are not authorised to use this service')
    }

    def created = {uid ->
        addLocation "/ws/dataResource/${uid}"
        render(status:201, text:'inserted entity')
    }

    def addLocation(relativeUri) {
        response.addHeader 'location', ConfigurationHolder.config.grails.serverURL + relativeUri
    }

    def badRequest = {text ->
        render(status:400, text: text)
    }

    def addContentLocation(relativeUri) {
        response.addHeader 'content-location', ConfigurationHolder.config.grails.serverURL + relativeUri
    }

    def success = { text ->
        render(status:200, text: text)
    }

    def notFound = { text ->
        render(status:404, text: text)
    }

    def addLastModifiedHeader = { when ->
        response.addHeader HttpHeaders.LAST_MODIFIED, rfc1123Format.format(when)
    }

    def renderJson = {json ->
        if (params.callback) {
            //render(contentType:'application/json', text: "${params.callback}(${json})")
            render(contentType:'text/javascript', text: "${params.callback}(${json})", encoding: "UTF-8")
        } else {
            render json
        }
    }

    def renderAsJson = {json ->
        renderJson(json as JSON)
    }

    def saveEntity = {
        def uid = params.uid
        def drt = params.drt
        def obj = params.json

        // inject the user name into the session so it can be used by audit logging if changes are made
        if (obj.user) {
            session.username = obj.user
        }

        def keyCheck = authService.checkApiKey(obj.api_key)
        if (!keyCheck.valid) {
            unauthorised()
        }
        else {
            if (drt) {
                // update
                crudService.updateTempDataResource(drt, obj)
                if (drt.hasErrors()) {
                    badRequest drt.errors
                } else {
                    addContentLocation "/ws/tempDataResource/${params.uid}"
                    success "updated entity"
                }
            }
            else {
                // create
                drt = crudService.insertTempDataResource(obj)
                if (drt.hasErrors()) {
                    badRequest drt.errors
                } else {
                    created drt.uid
                }
            }
        }
    }

    /**
     * Return JSON representation of specified entity
     * or list of entities if no uid specified.
     *
     * @param entity - controller form of domain class, eg dataProvider
     * @param uid - optional uid of an instance of entity
     * @param drt - optional instance specified by uid (added in beforeInterceptor)
     * @param summary - any non-null value will cause a richer summary to be returned for entity lists
     */
    def getEntity = {
        if (params.drt) {
            addContentLocation "/ws/tempDataResource/${params.drt.uid}"
            //addLastModifiedHeader params.drt.lastUpdated
            render crudService.readTempDataResource(params.drt)
        } else {
            addContentLocation "/ws/tempDataResource"
            def list = TempDataResource.list([sort:'name'])
            def summaries = list.collect {[name: it.name, uid: it.uid,email: it.email]}
            render summaries as JSON
        }
    }

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [tempDataResourceInstanceList: TempDataResource.list(params), tempDataResourceInstanceTotal: TempDataResource.count()]
    }

    def create = {
        def tempDataResourceInstance = new TempDataResource()
        tempDataResourceInstance.properties = params
        return [tempDataResourceInstance: tempDataResourceInstance]
    }

    def save = {
        def tempDataResourceInstance = new TempDataResource(params)
        if (tempDataResourceInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), tempDataResourceInstance.id])}"
            redirect(action: "show", id: tempDataResourceInstance.id)
        }
        else {
            render(view: "create", model: [tempDataResourceInstance: tempDataResourceInstance])
        }
    }

    def show = {
        def tempDataResourceInstance = TempDataResource.get(params.id)
        if (!tempDataResourceInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), params.id])}"
            redirect(action: "list")
        }
        else {
            [tempDataResourceInstance: tempDataResourceInstance]
        }
    }

    def edit = {
        def tempDataResourceInstance = TempDataResource.get(params.id)
        if (!tempDataResourceInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [tempDataResourceInstance: tempDataResourceInstance]
        }
    }

    def update = {
        def tempDataResourceInstance = TempDataResource.get(params.id)
        if (tempDataResourceInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (tempDataResourceInstance.version > version) {

                    tempDataResourceInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'tempDataResource.label', default: 'TempDataResource')] as Object[], "Another user has updated this TempDataResource while you were editing")
                    render(view: "edit", model: [tempDataResourceInstance: tempDataResourceInstance])
                    return
                }
            }
            tempDataResourceInstance.properties = params
            if (!tempDataResourceInstance.hasErrors() && tempDataResourceInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), tempDataResourceInstance.id])}"
                redirect(action: "show", id: tempDataResourceInstance.id)
            }
            else {
                render(view: "edit", model: [tempDataResourceInstance: tempDataResourceInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def tempDataResourceInstance = TempDataResource.get(params.id)
        if (tempDataResourceInstance) {
            try {
                tempDataResourceInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'tempDataResource.label', default: 'TempDataResource'), params.id])}"
            redirect(action: "list")
        }
    }
}
