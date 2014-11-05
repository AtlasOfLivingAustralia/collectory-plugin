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

import grails.converters.JSON

class MetadataService {

    static transactional = false

    // cache connection metadata
    def grailsApplication
    def connectionProfileMetadata = null
    def connectionParameterMetadata = null

    /**
     * Return object representing a connection profile with connection parameters.
     * @param profileName name of the profile
     */
    def getConnectionProfile(profileName) {
        checkConnectionMetadata()
        def pr = connectionProfileMetadata[profileName]
        // create a clone to hold the full param objects (so we don't change the cached profile)
        def clone = [name:pr.name, display:pr.display]
        if (pr) {
            clone.params = pr.params.collect { p ->
                connectionParameterMetadata[p.toString()]
            }
        }
        clone
    }

    def convertAnyLocalPaths(obj){
        def oldPath = "file:///" + grailsApplication.config.uploadFilePath
        def newPath = grailsApplication.config.grails.serverURL + grailsApplication.config.uploadExternalUrlPath
        if(obj in String){
            obj.replaceAll(oldPath, newPath)
        } else if(obj in JSON){
            obj.toString().replaceAll(oldPath, newPath)
        }
    }

    def convertPath(obj){
        def oldPath = "file:///" + grailsApplication.config.uploadFilePath
        def newPath = grailsApplication.config.grails.serverURL + grailsApplication.config.uploadExternalUrlPath
        obj.replaceAll(oldPath,newPath)
    }

    def getConnectionProfiles() {
        checkConnectionMetadata()
        return connectionProfileMetadata
    }

    def getConnectionProfilesAsList() {
        getConnectionProfiles().values().toList()
    }

    def getConnectionProfilesWithFileUpload() {
        getConnectionProfiles().values().toList().findAll({ it.supportFileUpload })
    }

    private checkConnectionMetadata() {
        if (!connectionProfileMetadata) {
            loadConnectionMetadata()
        }
    }

    private loadConnectionMetadata() {
        log.info "Loading connection profiles and parameters from disk"
        def json = new File("/data/collectory/config/connection-profiles.json").text
        def md = JSON.parse(json)
        // TODO: handle errors
        // load as map for quick lookup
        connectionProfileMetadata = md.profiles.inject([:]) {map, pr -> map << [(pr.name): pr]}
        connectionParameterMetadata = md.parameters.inject([:]) {map, pa -> map << [(pa.name): pa]}
    }

    def clearConnectionProfiles() {
        connectionProfileMetadata = null
    }

    def getConnectionParameters() {
        checkConnectionMetadata()
        return connectionParameterMetadata
    }

    def getConnectionParameter(name) {
        checkConnectionMetadata()
        return connectionParameterMetadata[name]
    }

    def clearConnectionParameters() {
        connectionParameterMetadata = null
    }
}
