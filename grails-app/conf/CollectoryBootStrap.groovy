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

            //CC0 licences
            new Licence(
                    acronym: 'CC0',
                    name:"Creative Commons Zero",
                    url:"https://creativecommons.org/publicdomain/zero/1.0/legalcode",
                    imageUrl: "https://licensebuttons.net/l/zero/1.0/88x31.png",
                    licenceVersion: "1.0"
            ).save(flush:true)

            ["4.0","3.0","2.5","2.0","1.0"].each { version ->
                new Licence(
                        acronym: 'CC-BY',
                        name:"Creative Commons Attribution",
                        url:"https://creativecommons.org/licenses/by/${version}/legalcode",
                        imageUrl: "https://licensebuttons.net/l/by/${version}/88x31.png",
                        licenceVersion: "${version}"
                ).save(flush:true)
                new Licence(
                        acronym: 'CC-BY-NC',
                        name:"Creative Commons Attribution-Noncommercial",
                        url:"https://creativecommons.org/licenses/by-nc/${version}/legalcode",
                        imageUrl: "https://licensebuttons.net/l/by-nc/${version}/88x31.png",
                        licenceVersion: "${version}"
                ).save(flush:true)
                new Licence(
                        acronym: 'CC-BY-SA',
                        name:"Creative Commons Attribution-Share Alike",
                        url:"https://creativecommons.org/licenses/by-sa/${version}/legalcode",
                        imageUrl: "https://licensebuttons.net/l/by-sa/${version}/88x31.png",
                        licenceVersion: "${version}"
                ).save(flush:true)
                new Licence(
                        acronym: 'CC-BY-NC-SA',
                        name:"Creative Commons Attribution-Noncommercial-Share Alike",
                        url:"https://creativecommons.org/licenses/by-nc-sa/${version}/legalcode",
                        imageUrl: "https://licensebuttons.net/l/by-nc-sa/${version}/88x31.png",
                        licenceVersion: "${version}"
                ).save(flush:true)
            }
        }
    }

    def destroy = {
    }
}