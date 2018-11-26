package au.org.ala.collectory

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.apache.tools.zip.ZipFile


@Transactional
class DataImportService {

    static final String EML_FILE = "eml.xml"

    def grailsApplication
    def metadataService, collectoryAuthService, idGeneratorService, emlImportService

    def serviceMethod() {}

    def reimportMetadataFromArchives(){
        int count = 0
        def jsonSlurper = new JsonSlurper()
        DataResource.findAll().each { dataResource ->
            if(dataResource.connectionParameters) {
                def json = jsonSlurper.parseText(dataResource.connectionParameters)
                if (json && json.protocol  && json.protocol == "DwCA") {
                    def archive = new File(json.url.replaceAll("file:////", "/"))
                    if (archive.exists()) {
                        importDataFileForDataResource(dataResource, archive, ["protocol": "DwCA"], false)
                    } else {
                        log.warn("Archive missing: ${json.url}")
                    }
                    count++
                }
            }
        }
        count
    }

    /**
     * Imports a directory filled with DwC-A files.
     *
     * @param directoryPath
     * @return
     */
    def importDirOfDwCA(String directoryPath){

        new File(directoryPath).listFiles().each { file ->
            if(file.getName().endsWith(".zip")){
                def dr = null
                def guid = getGuidFromDwCAFile(file)
                if(guid){
                    dr = DataResource.findByGuid(guid)
                }
                if(!dr){
                    //create a new data resource
                    dr = new DataResource(uid: idGeneratorService.getNextDataResourceId(),
                            name: "to be replaced",
                            userLastModified: collectoryAuthService.username()
                    )
                    dr.save(flush:true)
                }

                importDataFileForDataResource(dr, file, ["protocol":"DwCA"], true)
            }
        }
    }

    /**
     * Imports a file into the collectory (if migrate = true), assigning it to a data resource
     *
     * @param dataResource
     * @param protocol
     * @param termsForUniqueKey
     * @return
     */
    def importDataFileForDataResource(dataResource, filetoImport, params, migrate) {

        if(migrate) {
            def fileId = System.currentTimeMillis()
            def uploadDirPath = grailsApplication.config.uploadFilePath + fileId
            log.debug "Creating upload directory " + uploadDirPath
            def uploadDir = new File(uploadDirPath)
            FileUtils.forceMkdir(uploadDir)

            def newFile = null

            log.debug "Transferring file to directory...."
            if (filetoImport.metaClass.respondsTo(filetoImport, "transferTo")) {
                newFile = new File(uploadDirPath + File.separatorChar + filetoImport.getFileItem().getName())
                filetoImport.transferTo(newFile)
            } else {
                newFile = new File(uploadDirPath + File.separatorChar + filetoImport.getName())
                FileUtils.copyFile(filetoImport, newFile)
            }
            importDataFileForDataResource(dataResource, newFile, params)
        } else {
            importDataFileForDataResource(dataResource, filetoImport, params)
        }
    }

    /**
     * Import the supplied archive, using the existing file
     *
     * @param dataResource
     * @param newFile
     * @param params
     * @return
     */
    def importDataFileForDataResource(dataResource, newFile, params){

        //update the connection profile stuff
        def connParams = (new JsonSlurper()).parseText(dataResource.connectionParameters?:'{}')

        //retrieve any additional params
        def connProfile = metadataService.getConnectionProfile(params.protocol)
        def allConnParams = metadataService.getConnectionParameters()

        //resolve params
        connProfile.params.each { param ->
            def fullParamDescription = allConnParams.get(param.name)
            if(fullParamDescription.type == 'boolean'){
                connParams[param.paramName] = Boolean.parseBoolean(params[param.paramName])
            } else {
                connParams[param.paramName] = params[param.paramName]
            }
        }

        //termsForUniqueKey
        if(params.termsForUniqueKey){
            def origString = params.termsForUniqueKey.trim()
            def terms = []
            origString.split(',').each {
                terms << it.trim()
            }
            connParams.termsForUniqueKey = terms
        }

        connParams.url = 'file:///' + newFile.getPath()
        connParams.protocol = params.protocol

        //TODO it should be possible to retrieve the ID fields from the meta.xml
        if(!connParams.termsForUniqueKey ){
            connParams.termsForUniqueKey = ["occurrenceID"]
        }

        def contacts = []
        //for DWC-A, extract metadata from EML
        if(params.protocol == 'DwCA'){

            def zipFile = new ZipFile(newFile)
            zipFile.entries.each { file ->
                if (file.getName().startsWith(EML_FILE)) {
                    //open the XML file that contains the EML details for the GBIF resource
                    def xml = new XmlSlurper().parseText(zipFile.getInputStream(file).getText("UTF-8"))
                    contacts = emlImportService.extractFromEml(xml, dataResource)
                }
            }
        }

        dataResource.connectionParameters = (new JsonOutput()).toJson(connParams)
        dataResource.save(flush:true)

        //add contacts
        if(contacts){
            def existingContacts = dataResource.getContacts()
            contacts.each { contact ->
                def isNew = true
                existingContacts.each {
                    if (it.contact.email == contact.email) isNew = false
                }
                if (isNew) {
                    dataResource.addToContacts(contact, null, false, true, collectoryAuthService.username())
                }
            }
        }
    }

    def getGuidFromDwCAFile(newFile){
        def guid = null
        def zipFile = new ZipFile(newFile)
        zipFile.entries.each { file ->
            if (file.getName().startsWith(EML_FILE)) {

                //open the XML file that contains the EML details for the GBIF resource
                def xml = new XmlSlurper().parseText(zipFile.getInputStream(file).getText("UTF-8"))
                guid = xml.@packageId.toString()
            }
        }
        guid
    }
}
