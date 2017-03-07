package au.org.ala.collectory

class Licence {

    String acronym // e.g. 'CC BY'
    String name // 'Creative commons by Attribution'
    String licenceVersion //named licence version to avoid confusion with grails 'version'
    String url
    String imageUrl // 'URL to image to display'
    Date dateCreated
    Date lastUpdated


    public String toString() {
        if(licenceVersion)
            "${name} - ${acronym} - ${licenceVersion}"
        else
            "${name} - ${acronym}"
    }

    static constraints = {
        imageUrl nullable: true
    }
}
