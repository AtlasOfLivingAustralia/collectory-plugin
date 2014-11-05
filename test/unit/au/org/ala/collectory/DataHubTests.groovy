package au.org.ala.collectory

import grails.test.*

class DataHubTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testIsCollectionMember() {
        DataHub dh = new DataHub(uid: 'dh2', memberCollections:
        '[co9,co80,co10,co114,co112,co116,co113,co11,co136,co16,co21,co13,co117,co40,co39,co120,co119,co118,co115,co138,co41,co141,co142,co139,co137,co140,co42,co161,co34,co162,co157,co158,co156,co170,co159,co153,co154,co50,co51,co135,co155,co144,co146,co148,co152,co143,co151,co145,co147,co150,co165,co166,co127,co126,co125,co56,co57,co128,co111,co121,co77,co124,co123,co122,co76]')
        assert dh
        assert dh.isCollectionMember('co116')
    }
}
