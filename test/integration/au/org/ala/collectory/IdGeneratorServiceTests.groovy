package au.org.ala.collectory

import grails.test.*
import groovy.sql.Sql
import groovy.sql.GroovyRowResult
import au.org.ala.collectory.IdGeneratorService.IdType

class IdGeneratorServiceTests extends GrailsUnitTestCase {

    def idGeneratorService
    def sql

    protected void setUp() {
        super.setUp()

        // get the injected datasource
        sql = new Sql(idGeneratorService.getDataSource())

        // create the sequence table since it is not a domain class and is not set up automagically
        sql.execute("""
        create table sequence(
            id bigint primary key,
            next_id bigint not null,
            name varchar(45) not null,
            prefix varchar(5) not null)
        """)
        // plant seeds
        def insertSeed = "insert into sequence(id, next_id, name, prefix) values (?,?,?,?)"
        sql.execute(insertSeed, [1, 1, 'collection', 'co'])
        sql.execute(insertSeed, [2, 1, 'institution', 'in'])
        sql.execute(insertSeed, [3, 1, 'dataProvider', 'dp'])
        sql.execute(insertSeed, [4, 1, 'dataResource', 'dr'])
        sql.execute(insertSeed, [5, 1, 'dataHub', 'dh'])
        sql.execute(insertSeed, [6, 1, 'attribution', 'at'])
    }

    protected void tearDown() {
        sql.execute("drop table sequence")
        sql.close()
        super.tearDown()
    }

    void testGetNextId() {
        assertEquals "co1", idGeneratorService.getNextCollectionId()
        assertEquals "co2", idGeneratorService.getNextCollectionId()
        assertEquals "co3", idGeneratorService.getNextCollectionId()
        assertEquals "co4", idGeneratorService.getNextCollectionId()
        assertEquals "co5", idGeneratorService.getNextCollectionId()

        assertEquals "in1", idGeneratorService.getNextInstitutionId()
        assertEquals "dh1", idGeneratorService.getNextId(IdType.dataHub)
    }

    /**
     * This only works meaningfully if a runtime exception is thrown in the service method after the update is done.
     * This can be tested on a one off basis by putting the lines:
     *     if (type == IdType.dataProvider && next > 1)
     *         throw new RuntimeException()
     * in the service method just before the return statement
    void testGetNextCollectionIdRollback() {
        // should be 1
        assertEquals "dp1", idGeneratorService.getNextDataProviderId()

        String message = shouldFail(RuntimeException) {
            idGeneratorService.getNextId(IdType.dataProvider)
        }
        println "exception thrown: $message"

        // should be 2 not 3
        GroovyRowResult row = sql.firstRow("select next_id from sequence where id = ?",[IdType.dataProvider.getIndex()]) as GroovyRowResult
        assertEquals 2, row.next_id
    }
     */
}
