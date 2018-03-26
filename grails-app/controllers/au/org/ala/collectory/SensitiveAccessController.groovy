package au.org.ala.collectory

import grails.converters.JSON

/**
 * Retrieve the approvals in place for a user.
 */
class SensitiveAccessController {

    def index() { }

    def lookup(){
        def contact = Contact.findByUserId(params.userId)
        def approvals = [
                dataProviders:[]
        ]
        if(contact){
            ApprovedAccess.findByContact(contact).each {
                approvals.dataProviders << it.dataProvider.uid
            }
        }
        render approvals as JSON
    }
}
