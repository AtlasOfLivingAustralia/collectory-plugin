package au.org.ala.collectory

import grails.transaction.Transactional
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.apache.tools.zip.ZipFile

@Transactional
class DataImportService {

    static final String EML_FILE = "eml.xml"

    def grailsApplication
    def metadataService, collectoryAuthService, idGeneratorService

    def serviceMethod() {}

    /**
     * Imports a directory filled with DwC-A files.
     *
     * @param directoryPath
     * @return
     */
    def importDirOfDwCA(String directoryPath){
        new File(directoryPath).listFiles().each { file ->
            if(file.getName().endsWith(".zip")){
                def dr = new DataResource(uid: idGeneratorService.getNextDataResourceId(),
                        name: "to be replaced",
                        userLastModified: collectoryAuthService.username()
                )
                dr.save(flush:true)
                importDataFileForDataResource(dr, file, ["protocol":"DwCA"])
            }
        }
    }

    /**
     * Imports a file into the collectory, assigning it to a data resource
     *
     *
     * @param dataResource
     * @param protocol
     * @param termsForUniqueKey
     * @return
     */
    def importDataFileForDataResource(dataResource, filetoImport, params){

        def fileId = System.currentTimeMillis()
        def uploadDirPath = grailsApplication.config.uploadFilePath + fileId
        log.debug "Creating upload directory " + uploadDirPath
        def uploadDir = new File(uploadDirPath)
        FileUtils.forceMkdir(uploadDir)

        def newFile = null

        log.debug "Transferring file to directory...."
        if(filetoImport.metaClass.respondsTo(filetoImport, "transferTo")){
            newFile = new File(uploadDirPath + File.separatorChar + filetoImport.getFileItem().getName())
            filetoImport.transferTo(newFile)
        } else {
            newFile = new File(uploadDirPath + File.separatorChar + filetoImport.getName())
            FileUtils.copyFile(filetoImport, newFile)
        }

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


        //for DWC-A, extract metadata from EML
        if(params.protocol == 'DwCA'){

            def zipFile = new ZipFile(newFile)
            zipFile.entries.each { file ->
                if (file.getName().startsWith(EML_FILE)) {

                    //open the XML file that contains the EML details for the GBIF resource
                    def xml = new XmlSlurper().parseText(zipFile.getInputStream(file).getText("UTF-8"))
                    dataResource.guid = xml.@packageId.toString()
                    dataResource.pubDescription = xml.dataset?.abstract?.para
                    dataResource.name = xml.dataset.title.toString()
                    dataResource.email = xml.dataset.contact.electronicMailAddress.toString()
                    dataResource.rights = xml.dataset.intellectualRights?.para?.toString()
                    dataResource.websiteUrl = xml.dataset.metadataProvider?.onlineUrl

                    //TODO it should be possible to retrieve the ID fields from the meta.xml
                    if(!connParams.termsForUniqueKey ){
                        connParams.termsForUniqueKey = ["occurrenceID"]
                    }

                    //add a contacts...
                    if( xml.dataset.creator){
                        addContact(dataResource, xml.dataset.creator)
                    }
                    if( xml.dataset.metadataProvider
                            && xml.dataset.metadataProvider.electronicMailAddress != xml.dataset.creator.electronicMailAddress){
                        addContact(dataResource,  xml.dataset.metadataProvider)
                    }
                }
            }
        }

        dataResource.connectionParameters = (new JsonOutput()).toJson(connParams)
        dataResource.save(flush:true)
    }

    private def addContact(dataResource, emlElement){
        def contact = Contact.findByEmail(emlElement.electronicMailAddress)

        if(dataResource.contacts.contains(contact))
            return

        if(!contact){
            contact = new Contact()
            contact.firstName = emlElement.individualName.givenName
            contact.lastName = emlElement.individualName.surName
            contact.email = emlElement.electronicMailAddress
            contact.setUserLastModified(collectoryAuthService.username())
            contact.save(flush:true)
            contact.getErrors().each { println it }
        }
        dataResource.addToContacts(contact, null, false, true, collectoryAuthService.username())
    }
}
