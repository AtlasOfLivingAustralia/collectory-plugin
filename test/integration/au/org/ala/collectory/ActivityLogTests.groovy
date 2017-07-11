package au.org.ala.collectory

import grails.test.*
import grails.test.mixin.integration.Integration
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.util.Mixin
import spock.lang.Specification

@Integration
class ActivityLogTests extends Specification {
    def testCreation() {
        when:
        ActivityLog al = new ActivityLog(timestamp: new Date(), user: 'mark', action: "tested")
        al.validate()
        then:
        !al.hasErrors()
        al.toString().endsWith("mark tested")
    }

    def testSave() {
        when:
        ActivityLog al = new ActivityLog(timestamp: new Date(), user: 'mark', action: Action.VIEW.toString())
        al.save(flush:true, failOnError: true)
        then:
        !al.hasErrors()
        al.toString().endsWith("mark viewed")
        ActivityLog.findAll().size() == 1
        ActivityLog.findAllByAction('viewed').size() == 1
    }

    def testLog() {
        when:
        ActivityLog.log('mark', true, Action.LOGIN)
        then:
        ActivityLog.findAllByAction('logged in').size() == 1
    }
}
