package au.org.ala.collectory

class ApprovedAccess {

    Contact contact
    DataProvider dataProvider
    String dataResourceUids = "[]" //JSON array of dataResourceUids
    String taxonIDs = "[]" //JSON array of taxonIDs

    static constraints = {}
}
