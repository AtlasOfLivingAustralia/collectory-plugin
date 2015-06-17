import au.org.ala.collectory.Licence
import au.org.ala.collectory.Sequence

class CollectoryBootStrap {

    def grailsApplication

    def init = { servletContext ->

        //insert sequences
        def sequences = Sequence.findAll()
        if(!sequences){
            new Sequence(id: 1, name: "collection", prefix: "co", next_id: 0).save(flush:true)
            new Sequence(id: 1, name: "institution", prefix: "in", next_id: 0).save(flush:true)
            new Sequence(id: 1, name: "dataProvider", prefix: "dp", next_id: 0).save(flush:true)
            new Sequence(id: 1, name: "dataResource", prefix: "dr", next_id: 0).save(flush:true)
            new Sequence(id: 1, name: "dataHub", prefix: "dh", next_id: 0).save(flush:true)
            new Sequence(id: 1, name: "attribution", prefix: "at", next_id: 0).save(flush:true)
            new Sequence(id: 1, name: "tempDataResource", prefix: "drt", next_id: 0).save(flush:true)
        }

        //insert licences
        def licences = Licence.findAll()
        if(!licences){
            //insert default licences
            new Licence(name:"Creative Commons Attribution", url:"https://creativecommons.org/licenses/by/2.5/au/").save(flush:true)
            new Licence(name:"Creative Commons Attribution-Noncommercial", url:"https://creativecommons.org/licenses/by-nc/2.5/au/").save(flush:true)
            new Licence(name:"Creative Commons Attribution-Share Alike", url:"https://creativecommons.org/licenses/by-sa/2.5/au/").save(flush:true)
            new Licence(name:"Creative Commons Attribution-Noncommercial-Share Alike", url:"https://creativecommons.org/licenses/by-nc-sa/2.5/au/").save(flush:true)
        }
    }

    def destroy = {
    }
}