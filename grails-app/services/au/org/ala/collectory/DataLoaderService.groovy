package au.org.ala.collectory

import grails.converters.*;
import au.com.bytecode.opencsv.CSVReader
import au.org.ala.collectory.Geocoder.Location
import groovy.sql.Sql
import java.text.SimpleDateFormat

class DataLoaderService {

    def institutionCodeLoaderService
    def idGeneratorService
    javax.sql.DataSource dataSource

    boolean transactional = false

    /*****
     *****  Update providers from CSV file
     *****/

    def dataProvidercolumns = ["uid","name","pubDescription","address","websiteUrl","logoUrl","email","phone"]

    /**
     * Idempotent update of data providers from a tab-separated CSV file.
     *
     * @param filename the CSV file
     * @return an object that summaries the changes that result
     */
    def importDataProviders(String filename) {
        CSVReader reader = new CSVReader(new FileReader(filename),'\t' as char)
        String [] nextLine;
        int headerLines = 0
        int dataLines = 0
        int exists = 0
        int updates = 0
        int failures = 0
        int inserts = 0

        log.info "================================= importing providers ======="
		while ((nextLine = reader.readNext()) != null) {

            /* create a params map from the csv data
             * using the domain property names as keys
             */
            def params = [:]
            dataProvidercolumns.eachWithIndex {it, i ->
                //println "i=${i} value=${nextLine[i]}"
                if (nextLine[i]) {
                    // eliminate any \ used for line breaks
                    params[it] = nextLine[i] //.replaceAll("" + (92 as char)," ")
                }
            }

            if (params.name != 'name') {  // don't include the header row
                dataLines++
                DataProvider dp = DataProvider.findByUid(params.uid)
                if (!dp) {
                    // create it
                    dp = new DataProvider()
                    updateProvider dp, params
                    if (!dp.hasErrors() && dp.save(flush: true)) {
                        inserts++
                        log.info "Created data provider ${dp.name}"
                    } else {
                        failures++
                        log.info "Failed to create data provider ${params.name}"
                        dp.errors.each { log.info it }
                    }
                } else {
                    exists++
                    // update it
                    // TODO: limited testing
                    /* disable updateProvider dp, params
                    if (dp.isDirty()) {
                        if (!dp.hasErrors() && dp.save(flush: true)) {
                            updates++
                            log.info "Updated data provider ${dp.name}"
                        } else {
                            failures++
                            log.info "Failed to update data provider ${params.name}"
                            dp.errors.each { log.info it }
                        }
                    }*/
                }
            } else {
                headerLines++
            }
        }
        def returnObj = new Expando()
        returnObj.headerLines = headerLines
        returnObj.dataLines = dataLines
        returnObj.inserts = inserts
        returnObj.failures = failures
        returnObj.exists = exists
        returnObj.updates = updates
        return returnObj
    }

    def updateProvider = {dp, params ->
        def address = params.address
        params.remove('address')  // don't apply un-parsed address directly to entity
        if (hasValue(address)) {
            //println "---------------"
            //println "${params.uid} address = ${address}"
            def addressParams = parseAddress(address)
            /*addressParams.each {k,v ->
                println "${k} = ${v}"
            }*/
            if (dp.address) {
                // dp.address.properties = addressParams doesn't work - readonly for some reason
                dp.address.street = addressParams.street
                dp.address.postcode = addressParams.postcode
                dp.address.city = addressParams.city
                dp.address.state = addressParams.state
                dp.address.country = addressParams.country
                dp.address.postBox = addressParams.postBox
            } else {
                dp.address = new Address(addressParams)
            }
        }
        dp.properties['uid','name','pubDescription','websiteUrl','email','phone'] = params
        dp.userLastModified = "DR loader"
    }

    def hasValue = {it ->
        it && it.toString().toLowerCase() != 'null'
    }

    /*****
     *****  Update resources from CSV file
     *****/

    def dataResourcecolumns = ["uid","dataProvider","name","pubDescription","rights","citation","websiteUrl","logoUrl"]

    /**
     * Idempotent update of data resources from a CSV file.
     *
     * @param filename the CSV file
     * @return an object that summaries the changes that result
     */
    def importDataResources(String filename) {
        CSVReader reader = new CSVReader(new FileReader(filename),',' as char)
        String [] nextLine;
        int headerLines = 0
        int dataLines = 0
        int exists = 0
        int failures = 0
        int inserts = 0
        int updates = 0

		while ((nextLine = reader.readNext()) != null) {

            /* create a params map from the csv data
             * using the domain property names as keys
             */
            def params = [:]
            dataResourcecolumns.eachWithIndex {it, i ->
                //println "i=${i} value=${nextLine[i]}"
                if (nextLine[i]) {
                    // eliminate any \ used for line breaks
                    params[it] = nextLine[i] //.replaceAll("" + (92 as char)," ")
                }
            }

            if (params.name != 'name') {  // don't include the header row
                dataLines++
                DataResource dr = DataResource.findByUid(params.uid as String)
                if (!dr) {
                    // create it
                    dr = new DataResource()
                    updateResource dr, params
                    if (!dr.hasErrors() && dr.save(flush: true)) {
                        inserts++
                        log.info "Created data resource ${dr.name}"
                    } else {
                        failures++
                        log.error "Failed to create data resource ${params.name}"
                        dr.errors.each { log.error it }
                    }
                } else {
                    /* disable
                    // update it
                    // TODO: limited testing
                    updateResource dr, params
                    if (dr.isDirty()) {
                        updates++
                    }*/
                    exists++
                }
            } else {
                headerLines++
            }
        }
        def returnObj = new Expando()
        returnObj.headerLines = headerLines
        returnObj.dataLines = dataLines
        returnObj.inserts = inserts
        returnObj.failures = failures
        returnObj.exists = exists
        returnObj.updates = updates
        return returnObj
    }

    def updateResource = {dr, params ->
        dr.properties["uid","name","pubDescription","rights","citation","websiteUrl"] = params
        dr.dataProvider = DataProvider.findByUid(params.dataProvider as String)
        dr.userLastModified = "DR loader"
    }

    /** import to new structure **/
    /* BEFORE calling this you should import the contacts and provider codes (with the same ids as the import source) */
    def importJson() {
        def imp = JSON.parse(new FileInputStream('/data/collectory/bootstrap/export.json'), "UTF-8")
        def sql = new Sql(dataSource)

        // clear contactFor
        sql.execute("delete from contact_for")

        def contactMap = [:]
        imp.contactFor.each {
            // store in map until we load corresponding group
            def entityId = it.entityId
            List contactFors = contactMap.get(entityId) as List
            contactFors = (contactFors)?:[]
            contactFors.add  it
            contactMap.put(entityId, contactFors)
            //println "added contact ${it.contact.id} to entity ${entityId}"
        }
        /*imp.contact.each {
            Contact ct = new Contact()
            ct.id = it.id
            ct.firstName = load(it.firstName)
            ct.lastName = load(it.lastName)
            ct.phone = load(it.phone)
            ct.fax = load(it.fax)
            ct.title = load(it.title)
            ct.email = load(it.email)
            ct.userLastModified = load(it.userLastModified)
            ct.notes = load(it.notes)
            ct.mobile = load(it.mobile)
            // no point in these as Grails overrides them
            ct.dateCreated = loadDate(it.dateCreated)
            ct.lastUpdated = loadDate(it.dateLastModified)

            if (ct.hasErrors()) {
                ct.errors.each {println it}
            } else {
                ct.save()
            }
        }*/
        // clear institutions
        sql.execute("delete from provider_map_provider_code")
        sql.execute("delete from provider_map")
        sql.execute("delete from collection")
        sql.execute("delete from institution")

        // keep map of institutions keyed by original id so we can link collections
        def institutionMap = [:]

        imp.providerGroup.each {
            if (it.groupType == 'Institution') {
                def originalId = it.id
                log.info ">>processing ${it.name} original id = ${it.id}"

                Institution ins = new Institution()
                ins.name = it.name
                ins.institutionType = load(it.institutionType)
                ins.guid = load(it.guid)
                ins.uid = load(it.uid)
                ins.userLastModified = "Imported (previous update by ${it.userLastModified})"
                ins.isALAPartner = it.isALAPartner
                ins.networkMembership = it.networkMembership
                ins.address = loadAddress(it.address) as Address
                ins.logoRef = loadImage(it.logoRef) as Image
                ins.imageRef = loadImage(it.imageRef) as Image
                ins.latitude = it.latitude as BigDecimal
                ins.longitude = it.longitude as BigDecimal
                ins.phone = load(it.phone)
                ins.email = load(it.email)
                ins.acronym = load(it.acronym)
                ins.websiteUrl = load(it.websiteUrl)
                ins.state = load(it.state)
                ins.pubDescription = load(it.pubDescription)
                ins.techDescription = load(it.techDescription)

                ins.validate()
                if (ins.hasErrors()) {
                    ins.errors.each {log.info it}
                } else {
                    ins.save(flush:true)
                    // store in map until we load child collections
                    institutionMap.put(originalId, ins)

                    // add contacts
                    List contactFors = contactMap.get(originalId) as List
                    if (contactFors?.size()) {
                        log.info "found ${contactFors.size()} contacts for ${ins.name}"
                    }
                    contactFors.each{
                        ContactFor cf = new ContactFor(userLastModified: "Imported (previous update by ${it.userLastModified})")
                        Contact c = Contact.get(it.contact.id)
                        if (!c) {
                            log.error "failed to find contact with id = ${it.contact.id}"
                        } else {
                            cf.contact = c
                            cf.role = load(it.role)
                            cf.administrator = it.administrator
                            cf.primaryContact = it.primaryContact
                            cf.entityUid = ins.uid

                            cf.validate()
                            if (cf.hasErrors()) {
                                cf.errors.each {log.info it}
                            } else {
                                cf.save(flush:true)
                            }
                        }
                    }
                }
            }
        }

        // keep map of collections scopes keyed by original id so we can link to collections
        def scopeMap = [:]
        imp.collectionScope.each {
            scopeMap.put(it.id, it)
        }

        /* load collections */

        // keep map of collections keyed by original id so we can link providerMaps
        def collectionMap = [:]

        imp.providerGroup.each {
            if (it.groupType == 'Collection') {
                def originalId = it.id
                log.info ">>processing ${it.name} original id = ${it.id}"

                Collection col = new Collection()
                col.name = it.name
                col.guid = load(it.guid)
                col.uid = load(it.uid)
                col.userLastModified = "Imported (previous update by ${it.userLastModified})"
                col.isALAPartner = it.isALAPartner
                col.networkMembership = it.networkMembership
                col.address = loadAddress(it.address) as Address
                col.logoRef = loadImage(it.logoRef) as Image
                col.imageRef = loadImage(it.imageRef) as Image
                col.latitude = it.latitude as BigDecimal
                col.longitude = it.longitude as BigDecimal
                col.phone = load(it.phone)
                col.email = load(it.email)
                col.acronym = load(it.acronym)
                col.websiteUrl = load(it.websiteUrl)
                col.state = load(it.state)
                col.pubDescription = load(it.pubDescription)
                col.techDescription = load(it.techDescription)
                col.focus = load(it.focus)

                // load values from scope
                def scope = scopeMap.get(it.scope.id)
                if (scope) {
                    col.startDate = load(scope.startDate)
                    col.endDate = load(scope.endDate)
                    col.collectionType = load(scope.collectionType)
                    col.kingdomCoverage = load(scope.kingdomCoverage)
                    col.keywords = load(scope.keywords)
                    col.scientificNames = load(scope.scientificNames)
                    col.eastCoordinate = scope.eastCoordinate as BigDecimal
                    col.westCoordinate = scope.westCoordinate as BigDecimal
                    col.northCoordinate = scope.northCoordinate as BigDecimal
                    col.southCoordinate = scope.southCoordinate as BigDecimal
                    col.states = load(scope.states)
                    col.numRecords = scope.numRecords
                    col.numRecordsDigitised = scope.numRecordsDigitised
                    col.active = load(scope.active)
                    col.geographicDescription = load(scope.geographicDescription)
                    col.subCollections = load(scope.subCollections)
                } else {
                    log.info "warning: no scope for ${col.name}"
                }

                col.validate()
                if (col.hasErrors()) {
                    col.errors.each {log.info it}
                } else {
                    col.save(flush:true)
                    // store in map until we load provider maps
                    collectionMap.put(originalId, col)

                    // add institution
                    List insts = it.parents
                    switch (insts.size()) {
                        case 0:
                            log.info "No institution for ${col.name}"
                            break
                        case 1:
                            Institution inst = institutionMap.get(insts[0].id) as Institution
                            if (inst) {
                                col.institution = inst
                            }
                            log.info "added ${col.institution.name} as instn for ${col.name}"
                            break
                        default: log.info "Multiple parents for collection ${col.name}"
                    }

                    // add contacts
                    List contactFors = contactMap.get(originalId) as List
                    if (contactFors?.size()) {
                        log.info "found ${contactFors.size()} contacts for ${col.name}"
                    }
                    contactFors.each{
                        ContactFor cf = new ContactFor(userLastModified: "Imported (previous update by ${it.userLastModified})")
                        Contact c = Contact.get(it.contact.id)
                        if (!c) {
                            log.error "failed to find contact with id = ${it.contact.id}"
                        } else {
                            cf.contact = c
                            cf.role = load(it.role)
                            cf.administrator = it.administrator
                            cf.primaryContact = it.primaryContact
                            cf.entityUid = col.uid

                            cf.validate()
                            if (cf.hasErrors()) {
                                cf.errors.each {log.info it}
                            } else {
                                cf.save(flush:true)
                            }
                        }
                    }
                }
            }
        }

        /* load provider maps */

        imp.providerMap.each {
            ProviderMap pm = new ProviderMap()
            //pm.userLastModified = "Imported (previous update by ${it.userLastModified})"
            pm.exact = it.exact
            pm.matchAnyCollectionCode = it.matchAnyCollectionCode
            Collection col = collectionMap.get(it.providerGroup?.id) as Collection
            if (col) {
                pm.collection = col

                // institution codes
                List instCodes = it.institutionCodes
                instCodes.each {
                    pm.addToInstitutionCodes(ProviderCode.get(it.id))
                }
                // collection codes
                List collCodes = it.collectionCodes
                collCodes.each {
                    pm.addToCollectionCodes(ProviderCode.get(it.id))
                }
                pm.validate()
                if (pm.hasErrors()) {
                    pm.errors.each {log.info it}
                } else {
                    pm.save()
                }

            } else {
                log.info "failed to find collection with id = ${it.providerGroup?.id}"
            }
        }

        log.info "Imported ${Institution.count()} institutions."
        log.info "Imported ${Collection.count()} collections."
        log.info "Linked ${ContactFor.count()} contacts for entities."
        log.info "Linked ${ProviderMap.count()} provider maps for collections."
    }

    private Object load(Object it) {
        if (it) {
            if (it.toString() != 'null') {
                return it
            }
        }
        return null
    }

    private Object loadAddress(Object it) {
        if (it) {
            if (it.toString() != 'null') {
                Address ad = new Address()
                ad.street = load(it.street)
                ad.city = load(it.city)
                ad.postBox = load(it.postBox)
                ad.postcode = load(it.postcode)
                ad.state = load(it.state)
                ad.country = load(it.country)
                return ad
            }
        }
        return null
    }

    private Object loadImage(Object it) {
        if (it) {
            if (it.toString() != 'null') {
                Image img = new Image()
                img.file = load(it.file)
                img.caption = load(it.caption)
                img.attribution = load(it.attribution)
                img.copyright = load(it.copyright)
                return img
            }
        }
        return null
    }

    private Date loadDate(String it) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return sdf.parse(it.substring(0,it.length()-1))
    }




    /** old BCI imports **/

    /* List of field names as BCI names
     * LSID,RECORD_ID,CREATED,MODIFIED,NAME,CODE,KIND,TAXON_SCOPE,GEO_SCOPE,SIZE,SIZE_APPROX_INT,FOUNDED_YEAR,NOTES,CONTACT_PERSON,CONTACT_POSITION,CONTACT_PHONE,CONTACT_FAX,CONTACT_EMAIL,WEB_SITE,WEB_SERVICE_URI,WEB_SERVICE_TYPE,LOCATION_DEPARTMENT,LOCATION_STREET,LOCATION_POST_BOX,LOCATION_CITY,LOCATION_STATE,LOCATION_POSTCODE,LOCATION_COUNTRY_NAME,LOCATION_COUNTRY_ISO,LOCATION_LONG,LOCATION_LAT,LOCATION_ALT,LOCATION_NOTES,INSTITUTION_NAME,INSTITUTION_TYPE,INSTITUTION_URI,DESCRIPTION_TECH,DESCRIPTION_PUB,URL
     */
    // List of field names as domain property names (_NAx are not mapped)
    def column = ["guid","_NA1","_NA2","_NA3","name","acronym","kind","focus","geographicDescription","size","numRecords","startDate","notes","contactName","contactRole","contactPhone","contactFax","contactEmail","websiteUrl","webServiceUri","webServiceProtocol","_NA4","street","postBox","city","state","postcode","country","countryIsoCode","longitude","latitude","altitude","_NA6","institutionName","institutionType","institutionUri","techDescription","pubDescription","_NA7"]

    def loadContact(String firstName, String lastName, boolean publish) {
        Contact c = JSON.parse("""{
            firstName: "${firstName}",
            lastName: "$lastName",
            publish: """ + publish + "}") as Contact
        return c
    }

    def loadFromFile(String filename) {
        Contact c = JSON.parse(new FileInputStream(filename), "UTF-8") as Contact
        return c
    }

    def loadSupplementaryData(String filename, boolean overwriteAnyChanges, String user) {
        def cc = JSON.parse(new FileInputStream(filename), "UTF-8")
        cc.collections.each {
            ProviderGroup pg
            if (it.giud) pg = ProviderGroup.findByGuid(it.guid)
            else pg = ProviderGroup.findByName(it.name)

            boolean changed = false
            if (!pg) {
                // does not exist
                pg = new Collection(name: it.name, userLastModified: user, uid: idGeneratorService.getNextCollectionId())
                changed = true
            }
            // only update if last modified by BCI loader or json loader unless overwrite is true or just created
            if (overwriteAnyChanges || pg.userLastModified =~ "BCI loader" || pg.userLastModified =~ "json loader" || changed) {
                it.entrySet().each {
                    // handle BigDecimal
                    def value = it.value
                    if (value.endsWith(" as BigDecimal")) {
                        value = value.substring(0, value.length() - 14) as BigDecimal
                    }
                    // handle linked and embedded classes
                    if (it.key.indexOf('.') > 0) {
                        String[] bits = it.key.tokenize(".")
                        // create?
                        if (pg?."${bits[0]}" == null) {
                            // need to create - do it dumb for now
                            switch (bits[0]) {
                                case "address": pg?."${bits[0]}" = new Address(); break
                                case "imageRef": pg?."${bits[0]}" = new Image(); break
                                case "logoRef": pg?."${bits[0]}" = new Image(); break
                                // others we can't create simply so leave them
                            }
                        }
                        if (pg?."${bits[0]}"."${bits[1]}" != value) {
                            pg?."${bits[0]}"."${bits[1]}" = value
                            changed = true
                            // needs to update last mod for linked tables (but not embedded)
                            if (bits[0] in ["scope", "infoSource"]) {
                                pg?."${bits[0]}".dateLastModified = new Date()
                                pg?."${bits[0]}".userLastModified = "${user} (via json loader)"
                            }
                        }
                    } else {
                        if (pg?."${it.key}" != value) {
                            pg?."${it.key}" = value
                            changed = true
                        }
                    }
                }
                if (changed) {
                    pg?.userLastModified = "${user} (via json loader)"
                    pg?.dateLastModified = new Date()
                    pg?.save(flush:true)
                }
            }
        }
    }

    def loadAmrrnData() {
        CSVReader reader = new CSVReader(new FileReader("/data/collectory/bootstrap/amrrn.csv"))
        String [] nextLine;
        String [] columns = ["name","acronym","contact","address","phone","email"]
        while ((nextLine = reader.readNext()) != null) {

            /* create a params map from the csv data */
            def params = [:]
            columns.eachWithIndex {it, i ->
                params[it] = nextLine[i].trim()
            }

            if (params.name) {
                ProviderGroup pg = ProviderGroup.findByName(params.name)
                if (!pg) {
                    pg = new Collection(name: params.name, userLastModified: "AMRRN loader",
                            uid: idGeneratorService.getNextCollectionId(), acronym: params.acronym)
                    pg.address = new Address(parseAddress(params.address))
                    try {
                        Location loc = Geocoder.getLocation(params.address)
                        if (loc) {
                            pg.longitude = new BigDecimal(loc.lon)
                            pg.latitude = new BigDecimal(loc.lat)
                            log.info ">Long: ${pg.longitude} Lat: ${pg.latitude}"
                        }
                    } catch (NumberFormatException e) {
                        log.error "Unable to build lon/lat for ${params.address} - ${e.getMessage()}"
                    } catch (IOException e) {
                        log.error "Unable to get lon/lat for ${params.address} - ${e.getMessage()}"
                    }
                    pg.keywords = '["microbial"]'
                    pg.save(flush:true)
                    if (pg.hasErrors()) {
                        log.info pg.name + "- " + pg.errors
                    } else {
                        parseName(params)
                        Contact c = Contact.findByFirstNameAndLastName(params.firstName, params.lastName)
                        if (!c) {
                            c = new Contact(title: params.title, firstName: params.firstName, lastName: params.lastName,
                                phone: params.phone, email: params.email, userLastModified: "AMRRN loader")
                            c.save(flush:true)
                        }
                        if (c.hasErrors()) {
                            log.error c.lastName + "- " + c.errors
                        } else {
                            pg.addToContacts(c, "Curator", true, true, "AMRRN loader")
                            pg.save(flush:true)
                        }
                    }
                    log.info "${pg.name} ${pg.longitude} ${pg.latitude}"
                } else {
                    // update existing rough'n'ready
                    pg.address = new Address(parseAddress(params.address))
                    try {
                        Location loc = Geocoder.getLocation(params.address)
                        if (loc) {
                            pg.longitude = new BigDecimal(loc.lon)
                            pg.latitude = new BigDecimal(loc.lat)
                            log.info ">Long: ${pg.longitude} Lat: ${pg.latitude}"
                        }
                    } catch (NumberFormatException e) {
                        log.error "Unable to build lon/lat for ${params.address} - ${e.getMessage()}"
                    } catch (IOException e) {
                        log.error "Unable to get lon/lat for ${params.address} - ${e.getMessage()}"
                    }
                    pg.scope?.keywords = '["microbial"]'
                    pg.scope?.userLastModified = "AMRRN loader"
                    pg.scope?.dateLastModified = new Date()
                    pg.userLastModified = "AMRRN loader"
                    pg.dateLastModified = new Date()
                    pg.save(flush:true)
                    if (pg.hasErrors()) {
                        log.error pg.name + "- " + pg.errors
                    }
                }
            }
        }
    }

    def loadBCIData(String filename) {
        CSVReader reader = new CSVReader(new FileReader(filename))
        String [] nextLine;
        int institutionGuid = 1000

		while ((nextLine = reader.readNext()) != null) {

            /* create a params map from the csv data
             * using the domain property names as keys (ignoring blank and unmapped fields)
             */
            def params = [:]
            column.eachWithIndex {it, i ->
                if (!it.startsWith('_NA') && nextLine[i]) {
                    params[it] = nextLine[i]
                }
            }

            // only load aussie collections
            if (params.countryIsoCode == 'AU' || params.country == 'Australia' ||
                ['34908', '35014', '14847', '15596', '34932'].any {params.guid.endsWith(it)}) {

                /* contact */
                Contact contact = new Contact()
                contact.userLastModified = "BCI loader"
                contact.parseName(params.contactName)
                contact.phone = params.contactPhone
                contact.email = params.contactEmail
                contact.fax = params.contactFax
                contact.publish = true
                if (contact.hasContent()) {
                    contact.save(flush: true)
                    if (contact.hasErrors()) {
                        showErrors(params.name, contact.errors)
                    }
                } else {
                    contact = null
                }

                /*
                 * Logic here is:
                 *
                 * if record is an institution (rather than a collection)
                 *      if institution with same name exists (has already been loaded)
                 *          update properties
                 *      else
                 *          load institution-specific properties
                 * else
                 *      load collection-specific properties (creating an infosource if needed)
                 *
                 * validate and save
                 * add contact if there is one
                 * if record is a collection
                 *      if there is institution information
                 *          if the institution exists
                 *              update any empty fields
                 *          else
                 *              create it and save
                 *          add as parent to collection
                 * save
                 */
                ProviderGroup provider
                
                // check whether it's really an institution
                if (recogniseInstitution(params.name)) {
                    /* provider */
                    provider = new Institution()
                    // load some values
                    provider.properties["guid","name","acronym","focus","notes","websiteUrl","longitude","latitude",
                            "altitude","techDescription","pubDescription"] = params
                    provider.address = new Address()
                    provider.address.properties["street","postBox","city","state","postcode","country"] = params
                    provider.userLastModified = "BCI loader"

                    String institutionName = standardiseInstitutionName(provider.name)
                    /* institution */
                    // see if an institution with this name has already been saved
                    def institution = Institution.findByName(institutionName)
                    if (institution) {
                        // update it with these richer details
                        log.info "updating existing institution ${institution.name} with collection-level data"
                        institution.properties["guid","name","acronym","notes","websiteUrl","longitude","latitude",
                                "altitude","techDescription","pubDescription"] = params
                        institution.address = new Address()
                        institution.address.properties["street","postBox","city","state","postcode","country"] = params
                        provider.institutionType = massageInstitutionType(params.institutionType)
                        if (!provider.institutionType)
                            provider.institutionType = massageInstitutionType(params.kind)
                        institution.isALAPartner = isALAPartner(institution.name)
                        institution.userLastModified = "BCI loader"
                        // discard existing provider object and make this the provider (so we can do common processing later)
                        provider = institution
                    } else {
                        provider.uid = idGeneratorService.getNextInstitutionId()
                        provider.name = institutionName
                        provider.institutionType = massageInstitutionType(params.institutionType)
                        if (!provider.institutionType)
                            provider.institutionType = massageInstitutionType(params.kind)
                        // use AFD museum list to look up missing acronyms
                        if (!provider.acronym) {
                            String code = institutionCodeLoaderService.lookupInstitutionCode(provider.name)
                            if (code) {
                                log.info "Using code ${code} for institution ${provider.name}"
                                provider.acronym = code
                                // TODO: provider.providerCodes = code
                            }
                        }
                        provider.isALAPartner = isALAPartner(institutionName)
                    }
                } else {
                    /* provider */
                    provider = new Collection(uid: idGeneratorService.getNextCollectionId())
                    // load some values
                    provider.properties["guid","name","acronym","focus","notes","websiteUrl","longitude","latitude",
                            "altitude","techDescription","pubDescription"] = params
                    provider.address = new Address()
                    provider.address.properties["street","postBox","city","state","postcode","country"] = params
                    provider.userLastModified = "BCI loader"

                    /* collection */
                    provider.properties["geographicDescription","startDate"] = params
                    provider.keywords = extractKeywords(params)
                    provider.numRecords = buildSize(params)
                    provider.userLastModified = "BCI loader"

                    /*/ create or assign infosource only if there is some data
                    String webServiceUri = params.webServiceUri
                    String webServiceType = params.webServiceProtocol

                    if (webServiceUri) {
                        // see if there is an existing infosource that matches the access parameters
                        InfoSource is = InfoSource.list().find {
                            it.getWebServiceUri() == webServiceUri &&
                            it.getWebServiceProtocol() == webServiceType
                        }
                        if (!is) {
                            // create a new one
                            is = new InfoSource(title: "created for " + provider.name)
                            is.setWebServiceUri webServiceUri
                            is.setWebServiceProtocol webServiceType
                        }
                        provider.infoSource = is
                        is.addToCollections(provider)
                        is.userLastModified = "BCI loader"

                        is.save()
                        if (!is.validate()) {
                            is.errors.each {
                                println it
                            }
                        }
                    }*/

                }

                log.info ">> Loading ${provider?.name} as ${provider.groupType}"
                if (!provider.validate()) {
                    provider.errors.each {
                        log.error it
                    }
                }

                // save provider before linking contacts as we need the generated id
                provider.save(flush: true)
                if (provider.hasErrors()) {
                    showErrors(params.name, provider.errors)
                    // no point proceeding if the contact didn't save
                    continue;
                }

                // add contact if it exists and is ok
                if (contact && !contact.hasErrors()) {
                    String role = params.contactRole
                    // is the contact an editor?
                    // default to true
                    provider.addToContacts(contact, role?.empty ? "Contact" : role, true, true, "BCI loader")
                    // does not require a save
                }

                // only handle owning institutions if this is a collection
                if (provider instanceof Collection) {
                    /* institution */
                    String institutionName = standardiseInstitutionName(params.institutionName)
                    // only process if it has a name
                    ProviderGroup institution = null
                    if (institutionName) {
                        // see if it already exists
                        /* we should do this with guids but the BCI has no guids for institutions so just use name */
                        institution = ProviderGroup.findByName(institutionName)
                        if (institution != null) {
                            // update if blank
                            log.info ">> Updating institution ${institutionName} with type and uri and adding to collection ${provider.name}"
                            if (!institution.institutionType)
                                institution.institutionType = massageInstitutionType(params.institutionType)
                            if (!institution.websiteUrl)
                                institution.websiteUrl = params.institutionUri
                        } else {
                            log.info ">> Creating institution ${institutionName} for collection ${provider.name}"
                            institution = new Institution(uid: idGeneratorService.getNextInstitutionId())
                            institution.name = institutionName
                            // fudge the institution guid for now
                            institution.guid = institutionGuid++
                            institution.institutionType = massageInstitutionType(params.institutionType)
                            institution.websiteUrl = params.institutionUri
                            // use AFD museum list to look up acronyms
                            String code = institutionCodeLoaderService.lookupInstitutionCode(institution.name)
                            if (code) {
                                log.info "Using code ${code} for institution ${institution.name}"
                                institution.acronym = code
                            }
                            institution.isALAPartner = isALAPartner(institution.name)
                            institution.userLastModified = "BCI loader"
                            provider.save(flush: true)
                        }
                    }

                    // link collection to institution if we have one
                    if (institution) {
                        institution.addToChildren provider
                        institution.userLastModified = "BCI loader"

                        institution.save(flush: true)
                        if (institution.hasErrors()) {
                            showErrors(nextLine[BCI.LSID.ordinal()], institution.errors)
                        }

                        provider.addToParents institution
                        provider.save(flush: true)
                    }
                }
            }
		}

    }

    void showErrors(String label, Object errors) {
        log.info label
        errors.each {log.info it.toString()}
    }

    int parseInt(String s) {
        if (s == null)
            return ProviderGroup.NO_INFO_AVAILABLE
        return s?.empty ? ProviderGroup.NO_INFO_AVAILABLE : s as int
    }

    double parseDouble(String s) {
      return s?.empty ? ProviderGroup.NO_INFO_AVAILABLE : s as double
    }

    String buildLocation(params) {
        def map = ['Lat':params.latitude,
                   'Long':params.longitude,
                   'Alt':params.altitude]

        def strings = map.collect {key, value ->
            value?.empty ? '' : key + ": " + value + " "
        }

        return strings.join().trim()
    }

    int buildSize(params) {
        int size = parseInt(params.numRecords)
        if (size == ProviderGroup.NO_INFO_AVAILABLE) {
            // try to parse the text version of size in case it can be interpreted as a number
            String sizeStr = params.size
            if (sizeStr) {
                // remove spaces and commas
                def sizeTrim = ''
                sizeStr.each { item ->
                    if (('0'..'9').contains(item as char)) {
                        sizeTrim += item
                    }
                }
                try {
                    size = sizeTrim as int
                } catch (NumberFormatException e) {
                    // give up
                }
            }
        }
        return size
    }

    String massageInstitutionType(String bciType) {
        if (bciType) {
            String type = bciType.toLowerCase()
            if (ProviderGroup.constraints.institutionType.inList.contains(type)) {
                return type
            }
            if (type =~ "government") return "government"
            if (type =~ "museum") return "museum"
            if (type =~ "university") return "university"
            if (type =~ "herbarium") return "herbarium"
            log.info "Failed to massage institution type: ${bciType}"
        }
        return null
    }

    String massageState(String bciState) {
        switch (bciState.trim()) {
            case 'A.C.T.': return 'Australian Capital Territory'
            case 'ACT': return 'Australian Capital Territory'
            case 'N.S.W.': return 'New South Wales'
            case 'NSW': return 'New South Wales'
            case 'QLD': return 'Queensland'
            case 'NT': return 'Northern Territory'
            case 'Darwin': return 'Northern Territory'
            case 'WA': return 'Western Australia'
            case 'SA': return 'South Australia'
            case 'Southern Australia': return 'South Australia'
            case 'TAS': return 'Tasmania'
            case 'VIC': return 'Victoria'
        }
        return bciState.trim()
    }


    /**
     * Checks collection name to see if it is really a known institution.
     *
     * @param name to check
     * @return true if known
     */
    boolean recogniseInstitution(String name) {
        name = standardiseInstitutionName(name)
        return name in ['Commonwealth Scientific and Industrial Research Organisation',
                'Commonwealth Scientific and Industrial Research Organisation',
                'Tasmanian Museum and Art Gallery',
                'Museum Victoria',
                'Australian Museum',
                'South Australian Museum',
                'Tasmanian Department of Primary Industries and Water',
                'UWA Faculty of Natural & Agricultural Sciences',
                'Western Australian Department of Conservation & Land Management']
    }

    /**
     * Transforms institution name into a standardised form.
     *
     * @param name to check
     * @return standardised name
     */
    String standardiseInstitutionName(String name) {
        if (!name) return null
        name = name.trim()
        if (name[name.size() - 1] == '.')
            name = name.substring(0, name.size() - 1)
        switch (name) {
            case 'CSIRO': return 'Commonwealth Scientific and Industrial Research Organisation'
            case 'Australian Commonwealth Scientific and Research Organization (CSIRO)': return 'Commonwealth Scientific and Industrial Research Organisation'
            case 'Department of Tasmanian Museum and Art Gallery': return 'Tasmanian Museum and Art Gallery'
            case 'Museum of Victoria': return 'Museum Victoria'
            case 'Australian Museum': return 'Australian Museum'
            case 'South Australian Museum': return 'South Australian Museum'
            case 'Tasmanian Department of Primary Industries and Water': return 'Tasmanian Department of Primary Industries and Water'
            case 'UWA Faculty of Natural & Agricultural Sciences': return 'UWA Faculty of Natural & Agricultural Sciences'
            case 'Western Australian Department of Conservation & Land Management': return 'Western Australian Department of Conservation & Land Management'

            default: return name
        }
    }

    boolean isALAPartner(String name) {
        return name in [
                'Commonwealth Scientific and Industrial Research Organisation',
                'Australian Museum',
                'Queensland Museum',
                'Tasmanian Museum and Art Gallery',
                'Southern Cross University',
                'University of Adelaide',
                'Australian Government Department of Agriculture, Fisheries and Forestry',
                'Australian Government Department of the Environment, Water, Heritage and the Arts ',
                'Museum Victoria',
                'Museum of Victoria']
    }

    String extractKeywords(params) {
        List words = params.kind.tokenize("[, ()/]")
        words = words.collect{it.toLowerCase()}
        def keywords = words.findAll {!(it in ['and','not','specified'])}
        return (keywords as JSON).toString()
    }

    private void parseName(params) {
        // parse name
        def name = params.contact
        // remove any trailing parentheses  -  handles cases like "Mr Tom Weir (BSc (HONS))"
        if (name.indexOf('(') > 0) {
            name = name.substring(0, name.indexOf('('))
        }
        def title = ""
        def lastName = ""
        def firstName = ""

        def parts = name.split()
        switch (parts.size()) {
            case 0: break // bad
            case 1:
                lastName = name // only one word so make it last name
                break
            case 2:              // assume first + last
                firstName = parts[0]
                lastName = parts[1]
                break
            default:
                // cater for Dr Lemmy Caution and Lemmy A Caution
                /* Algorithm is:
                    - make first part the title if it is recognised
                    - make the last part the last name
                    - dump all the remaining parts into first name
                 */
                if (parts[0] == "Assoc" && parts[1] == "Prof") {  //special case
                    title = parts[0] + " " + parts[1]
                    firstName = parts[2..parts.size() - 2].join(" ")
                } else if (parts[0] in ["Dr", "Dr.", "Prof", "Mr", "Ms", "Mrs"]) {
                    title = parts[0]
                    firstName = parts[1..parts.size() - 2].join(" ")
                } else {
                    title = ''
                    firstName = parts[0..parts.size() - 2].join(" ")
                }
                lastName = parts[parts.size() - 1]
                break
        }
        if (title) {params.title = title}
        if (firstName) {params.firstName = firstName}
        params.lastName = lastName
    }

    private Map parseAddress(address) {

        def trimTrailing = {it ->
            it = it.trim()
            if (it[it.length()-1] == ',') {
                it = it[0..it.length()-2]
            }
            it = it.trim()
            return it
        }

        def lastWord = {it ->
            return it.trim().tokenize(' ,').last().trim()
        }

        def removeLastWord = {it ->
            def lastIndex = Math.max(it.lastIndexOf(' '),it.lastIndexOf(','))
            it = it[0..lastIndex-1]
            it = trimTrailing(it)
            return it
        }

        def params = [:]
        // parse address  eg Plant Pathology Branch, DPIand Fisheries, 80 meiers Rd, Indooroopilly, QLD 4069
        address = trimTrailing(address)
        //println "---------------"
        //println "full address = " + address

        /* remove possible country at end */
        ['australia','new zealand'].each { country ->
            if (lastWord(address).toLowerCase() == country) {
                address = removeLastWord(address)
            }
        }

        /* POSTCODE */
        def postcode = address[address.length() - 4 .. address.length() - 1]
        //println "possible postcode = " + postcode
        try {
            Integer.parseInt(postcode)
            address = address[0 .. address.length() - 5]
            address = trimTrailing(address)
        } catch (NumberFormatException e) {
            postcode = ""
        }

        /* STATE */
        def state = ""
        if (address[address.length()-2..address.length()-1] in ['WA', 'SA', 'NT', 'wa', 'sa', 'nt']) {
            state = address[address.length()-2..address.length()-1]
            address = address[0 .. address.length()-3]
        } else if (address[address.length()-3..address.length()-1].toLowerCase() in ['nsw', 'act', 'qld', 'vic', 'tas']) {
            state = address[address.length()-3..address.length()-1]
            address = address[0 .. address.length()-4]
        } else if (address.toLowerCase().endsWith('tasmania')) {
            state = 'TAS'
            address = address[0 .. address.length()-9]
        } else if (address.toLowerCase().endsWith('south australia')) {
            state = 'SA'
            address = address[0 .. address.length()-16]
        }
        state = massageState(state.toUpperCase())

        /* CITY or SUBURB */
        def city = ""
        address = trimTrailing(address)

        def bits = address.tokenize(",")
        /*println "<<bits"
        bits.each {println it.trim()}
        println "bits>>"*/

        if (bits.size > 1) {
            // if remainder contains a comma then assume the right-most bit is the city
            city = bits.last()
            bits = bits - bits.last()
            // or bits.remove(bits.size()-1)
        } else {
            // can only assume that right-most word is the city - this will fail on ambiguous strings
            city = lastWord(address)
            address = removeLastWord(address)
            bits = [address]
        }

        def postBox = ""
        def index = -1
        bits.eachWithIndex {it, i ->
            if (it.trim().startsWith('PO Box') || it.trim().startsWith('Locked Bag') || it.trim().startsWith('GPO')) {
                postBox = it.trim()
                index = i
            }
        }
        if (index > 0) {
            bits.remove(index)
        }
        def street = bits.join(', ')

        if (street) {params.street = street}
        if (city) {params.city = city}
        if (state) {params.state = state}
        if (postcode) {params.postcode = postcode}
        if (postBox) {params.postBox = postBox}

        /*params.each {k,v ->
            println "${k} = ${v}"
        }*/

        return params
    }
}
