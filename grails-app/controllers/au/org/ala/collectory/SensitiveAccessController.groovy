package au.org.ala.collectory

import grails.converters.JSON
import groovy.json.JsonSlurper

/**
 * Retrieve the approvals in place for a user.
 */
class SensitiveAccessController {

    def index() { }

    def lookup(){
        def contact = Contact.findByUserId(params.userId)
        def approvals = [
                dataProviders:[],
                dataResources:[]
        ]
        if(contact){
            ApprovedAccess.findAllByContact(contact).each {

                approvals.dataProviders << it.dataProvider.uid

                def approvedAccessUids = new JsonSlurper().parseText(it.dataResourceUids?:"[]")
                if(approvedAccessUids == "[]"){
                    approvedAccessUids = []
                }

                if(approvedAccessUids){
                    // a list has been specified, use this
                    approvals.dataResources.addAll(approvedAccessUids)
                } else {
                    //no list, add all resources for this provider
                    it.dataProvider.getResources().each {
                        approvals.dataResources << it.uid
                    }
                }
            }
        }
        render approvals as JSON
    }
}
