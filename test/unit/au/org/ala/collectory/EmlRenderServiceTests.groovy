package au.org.ala.collectory

import grails.test.mixin.*
import grails.test.mixin.web.ControllerUnitTestMixin
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.context.MessageSource
import spock.lang.Specification

import java.sql.Timestamp
import java.text.SimpleDateFormat

@TestFor(EmlRenderService)
@TestMixin(ControllerUnitTestMixin)
@Mock([DataLink, Attribution, ContactFor])
class EmlRenderServiceTests extends Specification {
    static ORGANIZATION1 = "ALA"
    static ID1 = 2000
    static GUID1 = 'urn:x-test:tdr100'
    static UID1 = 'tdr100'
    static NAME1 = 'Test data resource'
    static ACRONYM1 = 'TDR'
    static PUBDESC1 = 'A public description'
    static TECHDESC1 = 'A technical description'
    static FOCUS1 = 'Focus'
    static STATE1 = 'Victoria'
    static ADDRESS1 = new Address(street: '101 Collins St', city: 'Melbourne', state: STATE1, postcode: '3000')
    static LATITUDE1 = -35.55
    static LONGITUDE1 = 148.66
    static ALTITUDE1 = '100m'
    static WEBSITEURL1 = 'http://localhost:8080/test'
    static EMAIL1 = 'nobody@localhost'
    static PHONE1 = '(99) 8888 7777'
    static NOTES1 = 'Some notes'
    static ATTRIBUTIONS1 = 'co100 co101'
    static RIGHTS1 = 'Copyright CSIRO 2015'
    static CITATION1 = 'Some random person 2015'
    static LICENCETYPE1 = DataResource.creativeCommonsLicenses[0]
    static LICENCEVERSION1 = '3.0'
    static LASTUPDATED1 = new Date(100000000)

    def service = new EmlRenderService()
    def dr

    protected void setup() {
        grailsApplication.config.eml.organizationName = "Atlas of Living Australia (ALA)"
        grailsApplication.config.eml.deliveryPoint = "CSIRO Black Mountain Laboratories, Clunies Ross Street, ACTON"
        grailsApplication.config.eml.city = "Canberra"
        grailsApplication.config.eml.administrativeArea = "ACT"
        grailsApplication.config.eml.postalCode = "2601"
        grailsApplication.config.eml.country = "Australia"
        grailsApplication.config.eml.electronicMailAddress = "info@ala.org.au"
        service.grailsApplication = grailsApplication
        service.messageSource = Mock(MessageSource)
        service.messageSource.getMessage(_, _, _, _) >> { code, args, deflt, locale -> deflt }
        dr = new DataResource(
                guid: GUID1,
                uid: UID1,
                name: NAME1,
                acronym: ACRONYM1,
                pubDescription: PUBDESC1,
                techDescription: TECHDESC1,
                focus: FOCUS1,
                address: ADDRESS1,
                latitude: LATITUDE1,
                longitude: LONGITUDE1,
                altitude: ALTITUDE1,
                state: STATE1,
                websiteUrl: WEBSITEURL1,
                email: EMAIL1,
                phone: PHONE1,
                notes: NOTES1,
                attributions: ATTRIBUTIONS1,
                rights: RIGHTS1,
                citation: CITATION1,
                licenseType: LICENCETYPE1,
                licenseVersion: LICENCEVERSION1
        )
        dr.id = ID1
        dr.lastUpdated = LASTUPDATED1
    }

    void testDataResource1() {
        when:
        String emls = service.emlForResource(dr)
        XmlSlurper slurper = new XmlSlurper()
        def eml = slurper.parse(new StringReader(emls))
        println emls
        then:
        eml != null
        def dataset = eml.dataset
        dataset.alternateIdentifier?.find({it == "org.ala.au:${UID1}"}) != null
        dataset.title == NAME1
        dataset.pubDate == (new SimpleDateFormat('EEE MMM dd')).format(LASTUPDATED1)
        dataset.abstract?.para == "${PUBDESC1}\n${TECHDESC1}"
        dataset.creator != null
        dataset.creator.organizationName == NAME1
        dataset.creator.address != null
        dataset.creator.address.deliveryPoint == ADDRESS1.street
        dataset.creator.phone == PHONE1
        dataset.creator.electronicMailAddress.text() == EMAIL1
        dataset.creator.onlineUrl == WEBSITEURL1
        dataset.intellectualRights != null
        dataset.intellectualRights.section.size() == 3
        dataset.intellectualRights.section[0].title == "Rights"
        dataset.intellectualRights.section[0].para == RIGHTS1
        dataset.intellectualRights.section[1].title == "Citation"
        dataset.intellectualRights.section[1].para == CITATION1
        dataset.intellectualRights.section[2].title == "License"
        dataset.intellectualRights.section[2].para == "Creative Commons Attribution 3.0"
        dataset.contact?.organizationName == grailsApplication.config.eml.organizationName
    }


    void testDataResource2() {
        when:
        dr.rights = null
        dr.licenseType = 'other'
        dr.licenseVersion = null
        String emls = service.emlForResource(dr)
        XmlSlurper slurper = new XmlSlurper()
        def eml = slurper.parse(new StringReader(emls))
        then:
        eml != null
        def dataset = eml.dataset
        dataset.alternateIdentifier?.find({it == "org.ala.au:${UID1}"}) != null
        dataset.title == NAME1
        dataset.pubDate == (new SimpleDateFormat('EEE MMM dd')).format(LASTUPDATED1)
        dataset.abstract?.para == "${PUBDESC1}\n${TECHDESC1}"
        dataset.creator != null
        dataset.creator.organizationName == NAME1
        dataset.creator.address != null
        dataset.creator.address.deliveryPoint == ADDRESS1.street
        dataset.creator.phone == PHONE1
        dataset.creator.electronicMailAddress == EMAIL1
        dataset.creator.onlineUrl == WEBSITEURL1
        dataset.intellectualRights != null
        dataset.intellectualRights.section.size() == 1
        dataset.intellectualRights.section[0].title == "Citation"
        dataset.intellectualRights.section[0].para == CITATION1
        dataset.contact?.organizationName == grailsApplication.config.eml.organizationName
    }

}
