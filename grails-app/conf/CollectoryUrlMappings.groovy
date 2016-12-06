class CollectoryUrlMappings {
    static mappings = {
        name standard: "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }

        "/data/$directory/$file" {
            controller = 'data'
            action = 'serveFile'
            constraints {
                directory(inList:['taxa','collection','institution','dataProvider','dataResource','tempDataResource','dataHub','network'])
            }
        }

        "/upload/$directory/$file" {
            controller = 'data'
            action = 'fileDownload'
            constraints {}
        }

        "/ws/upload/$directory/$file" {
            controller = 'data'
            action = 'fileDownload'
            constraints {}
        }

        "/ws/licence" (controller:'licence')

        "/rif-cs(.$format)?" (controller:'dataFeeds',action:'index')
        "/ws/rif-cs(.$format)?" (controller:'dataFeeds',action:'index')

        "/feed(.$format)?" (controller:'dataFeeds',action:'rssFeed')

        // temporary mock notification service
        "/ws/notify" (controller:'data', action:'notify')

        "/lookup/inst/$inst/coll/$coll(.$format)?"(controller:'lookup',action:'collection')
        "/ws/lookup/inst/$inst/coll/$coll(.$format)?"(controller:'lookup',action:'collection')
        "/ws/lookup/$action/$id"(controller:'lookup')
        "/ws/lookup/$action"(controller:'lookup')


        "/admin" (controller:'manage',action:'list')

        "/admin/export/$table" (controller:'admin',action:'export')

        // data sets
        "/datasets" (controller: 'public', action: 'dataSets')

        // self-service
        //"/manage" (controller: 'manage', action: 'index')

        // data services
        "/ws/$entity/count/$groupBy?" (controller:'data', action: 'count') {
            constraints {
                entity(inList:['collection','institution','dataProvider','dataResource','tempDataResource','dataHub'])
            }
        }

        "/ws/$entity/$uid?(.$format)?" (controller:'data') {
            action = [HEAD: 'head', GET:'getEntity', PUT:'saveEntity', DELETE:'delete', POST:'saveEntity']
            constraints {
                entity(inList:['collection','institution','dataProvider','dataResource','tempDataResource','dataHub'])
            }
        }

        "/ws/$entity/summary(.$format)?" (controller:'data') {
            action = [HEAD: 'head', GET:'getEntity', PUT:'saveEntity', DELETE:'delete', POST:'saveEntity']
            constraints {
                entity(inList:['collection','institution','dataProvider','dataResource','tempDataResource','dataHub'])
                summary = 'true'
            }
        }

        // data resource harvesting parameters
        "/ws/dataResource/$uid/connectionParameters(.$format)?" (controller:'data', action:'connectionParameters')

        // raw contact data
        //"/ws/contacts/$id?" (controller: 'data', action: 'contacts')
        "/ws/contacts/email/$email?" (controller: 'data', action: 'getContactByEmail')

        "/ws/contacts/$id(.$format)?" (controller:'data') {
            action = [GET:'contacts', PUT:'updateContact', DELETE:'deleteContact', POST:'updateContact']
        }

        // entities that can be edited by a contact
        "/ws/contacts/$id/authorised(.$format)?" (controller: 'data', action: 'authorisedForContact')

        // entity contacts
        "/ws/$entity/$uid/contacts/$id?" {
            controller = 'data'
            action = [GET:'contactForEntity', PUT:'updateContactFor', POST:'updateContactFor', DELETE: 'deleteContactFor']
            constraints {
                entity(inList:['collection','institution','dataProvider','dataResource','tempDataResource','dataHub'])
            }
        }

        // all contacts for an entity type
        // the next 5 rules should be able to be expressed as one rule in the same format as above
        // BUT /ws/$entity/contact does not work for some reason
        "/ws/collection/contacts(.$format)?" { controller = 'data'; action = 'contactsForEntities'; entity = 'collection'}
        "/ws/institution/contacts(.$format)?" { controller = 'data'; action = 'contactsForEntities'; entity = 'institution'}
        "/ws/dataProvider/contacts(.$format)?" { controller = 'data'; action = 'contactsForEntities'; entity = 'dataProvider'}
        "/ws/dataResource/contacts(.$format)?" { controller = 'data'; action = 'contactsForEntities'; entity = 'dataResource'}
        "/ws/tempDataResource/contacts(.$format)?" { controller = 'data'; action = 'contactsForEntities'; entity = 'tempDataResource'}
        "/ws/dataHub/contacts(.$format)?" { controller = 'data'; action = 'contactsForEntities'; entity = 'dataHub'}

        // contacts to be notified on entity instance event
        "/ws/$entity/$uid/contacts/notifiable" {
            controller = 'data'
            action = 'notifyList'
            constraints {
                entity(inList:['collection','institution','dataProvider','dataResource','tempDataResource','dataHub'])
            }
        }

        "/ws/$entity/$uid/contacts(.$format)?" {
            controller = 'data'
            action = 'contactsForEntity'
            constraints {
                entity(inList: ['collection', 'institution', 'dataProvider', 'dataResource', 'tempDataResource', 'dataHub'])
            }
        }

        "/ws/$entity/$uid/contact(.$format)?" {
            controller = 'data'
            action = 'contactForEntity'
            constraints {
                entity(inList: ['collection', 'institution', 'dataProvider', 'dataResource', 'tempDataResource', 'dataHub'])
            }
        }

        // html fragment representation
        "/ws/fragment/$entity/$uid" {
            controller = 'data'
            action = [HEAD: 'head', GET:'getFragment']
            constraints {
                entity(inList:['collection','institution'])
            }
        }

        // entities in a data hub
        "/ws/dataHub/$uid/institutions(.$format)?" (controller: 'data', action: 'institutionsForDataHub')
        "/ws/dataHub/$uid/collections(.$format)?" (controller: 'data', action: 'collectionsForDataHub')

        // citations
        "/ws/citations/$include?" (controller:'lookup', action:'citations')

        // download limits
        "/ws/downloadLimits" (controller:'lookup', action:'downloadLimits')

        // eml
        "/eml/$id?" (controller:'data',action:'eml')
        // preferred
        "/ws/eml/$id?" (controller:'data',action:'eml')

        // GBIF IPT
        "/ws/ipt/scan/$uid(.$format)?" (controller: 'ipt', action: 'scan')

        // high-performance name lookup from uid list
        "/ws/resolveNames/$uids" (controller: 'data', action: 'resolveNames')

        "/lookup/summary/$id(.$format)?" (controller:'lookup',action:'summary')

        "/ws/collection/contacts/$uid(.$format)?" (controller:'data',action:'contactsForCollections')
        "/ws/institution/contacts/$uid(.$format)?" (controller:'data',action:'contactsForInstitutions')
        "/ws/dataProvider/contacts/$uid(.$format)?" (controller:'data',action:'contactsForDataProviders')
        "/ws/dataResource/contacts/$uid(.$format)?" (controller:'data',action:'contactsForDataResources')
        "/ws/dataHub/contacts/$uid(.$format)?" (controller:'data',action:'contactsForDataHubs')
        "/ws" (controller:'data',action:'catalogue')
        "/showConsumers/$id(.$format)?" (controller:'entity',action:'showConsumers')
        "/showProviders/$id(.$format)?" (controller:'entity',action:'showProviders')

        "/ws/codeMapDump" (controller:'data', action:'codeMapDump')

        "/ws/dataResource/harvesting(.$format)?" (controller:'reports', action: 'harvesters')

        "/ws/$entity?(.$format)?" (controller:'data', action:[GET:'getEntity', POST: 'saveEntity',  PUT:'saveEntity', DELETE: 'delete'])

        "/public/resources(.$format)"(controller:'public', action:'resources')

        "/"(controller:'public', action:'map')

        "500"(view:'/error')
    }
}
