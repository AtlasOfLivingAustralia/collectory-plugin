package au.org.ala.collectory

class ApprovedAccess implements Serializable {

    static auditable = [ignore: ['version','dateCreated','lastUpdated','userLastModified']]

    Contact contact
    DataProvider dataProvider
    String dataResourceUids = "[]" //JSON array of dataResourceUids
    String taxonIDs = "[]" //JSON array of taxonIDs

    Date dateCreated
    Date lastUpdated
    String userLastModified

    static mapping  = {
        dataResourceUids type: "text"
        taxonIDs type: "text"
    }

    static constraints = {}
}
