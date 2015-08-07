package au.org.ala.collectory

import grails.test.mixin.TestFor
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(DataConnection)
@Mixin(DomainClassUnitTestMixin)
class DataConnectionSpec extends Specification {
    DataResource dataResource
    def parameters
    DataConnection connection

    def setup() {
        dataResource = new DataResource()
        parameters = [
                protocol: 'DwC',
                location: 'http://nowhere.com',
                lineDelimiter: '\n',
                valueDelimiter: ',',
                keywords: ["my", "word"],
        ]
        connection = new DataConnection(
                dataResource: dataResource,
                sequence: 1,
                parameters: parameters
        )
    }

    def cleanup() {
    }

    void testValidate1() {
        when:
        def val = this.connection.validate()
        connection.errors.allErrors.each { println it }
        then:
        val == true
    }

    void testSave1() {
        setup:
        def saved = this.connection.save([flush: true])
        when:
        def restored = DataConnection.findBySequence(1)
        then:
        restored != null
        restored.id > 0
        restored.dataResource != null
        restored.sequence == connection.sequence
        restored.parameters == parameters
    }
}
