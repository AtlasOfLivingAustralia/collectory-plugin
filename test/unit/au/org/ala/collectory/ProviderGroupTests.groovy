package au.org.ala.collectory

import grails.test.*

class ProviderGroupTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testLatitude() {
        ProviderGroup pg = new Collection(guid: '237645', name: 'Bees', latitude: -40.03234665)
        assertEquals(-40.03234665, pg.latitude)
    }

    void testAttributions() {
        Collection pg = new Collection(guid:'1234',name:'Bees')
        assertEquals 0, pg.getAttributionList().size()
        pg.addAttribution 'at1'
        assertEquals 'at1', pg.attributions
        pg.addAttribution 'at2'
        assertEquals 'at1 at2', pg.attributions
        pg.addAttribution 'at1'
        assertEquals 'at1 at2', pg.attributions

        assertTrue pg.hasAttribution('at2')
        assertFalse pg.hasAttribution('at3')

        pg.removeAttribution 'at1'
        assertFalse pg.hasAttribution('at1')
        assertTrue pg.hasAttribution('at2')

        pg.removeAttribution 'at2'
        assertEquals 0, pg.getAttributionList().size()
    }

    void testMakeAbstract() {
        ProviderGroup pg = new Institution(guid: '237645', name: 'Bees')
        pg.pubDescription = "Taxonomic research on Antarctic."
        assertEquals 'Taxonomic research on Antarctic.', pg.makeAbstract(400)
        pg.pubDescription = "Stuff.\nTaxonomic research on Antarctic.\n"
        assertEquals 'Stuff. Taxonomic research on Antarctic.', pg.makeAbstract(400)
        pg.pubDescription = "Taxonomic research on Antarctic.\nStuff."
        assertEquals 'Taxonomic research on Antarctic. Stuff.', pg.makeAbstract(400)
        pg.pubDescription = "Taxonomic research on Antarctic.\nStuff.\n"
        assertEquals 'Taxonomic research on Antarctic. Stuff.', pg.makeAbstract(400)
        pg.pubDescription = "Taxonomic research on Antarctic.\nStuff.\nMore stuff."
        assertEquals 'Taxonomic research on Antarctic. Stuff.', pg.makeAbstract(400)
    }
}
