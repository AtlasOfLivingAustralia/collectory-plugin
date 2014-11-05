package au.org.ala.collectory

import grails.test.*

class ActivityLogTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
        mockDomain ActivityLog
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCreation() {
        ActivityLog al = new ActivityLog(timestamp: new Date(), user: 'mark', action: "tested")
        assertNotNull al
        al.errors.each {println it}
        assertFalse al.hasErrors()

        assertTrue al.toString().endsWith("mark tested")
    }

    void testSave() {
        ActivityLog al = new ActivityLog(timestamp: new Date(), user: 'mark', action: Action.VIEW.toString())
        assertNotNull al
        al.errors.each {println it}
        assertFalse al.hasErrors()
        al.save(flush:true)
        assertTrue al.toString().endsWith("mark viewed")
        assertEquals 1, ActivityLog.findAll().size()
        assertEquals 1, ActivityLog.findAllByAction('viewed').size()
    }

    void testLog() {
        ActivityLog.log('mark', true, Action.LOGIN)
        assertEquals 1, ActivityLog.findAllByAction('logged in').size()
    }
}
