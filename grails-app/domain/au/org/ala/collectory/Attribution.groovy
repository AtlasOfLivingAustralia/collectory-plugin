package au.org.ala.collectory

class Attribution {

    String name
    String url
    String uid

    static constraints = {
        name(blank:false, maxSize: 256)
        url(nullable:true, maxSize: 256)
        uid(blank:false, maxSize: 20)
    }

}
