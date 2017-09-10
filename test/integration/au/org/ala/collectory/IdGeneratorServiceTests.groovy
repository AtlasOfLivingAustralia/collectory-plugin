package au.org.ala.collectory

import grails.test.*
import grails.test.mixin.integration.Integration
import groovy.sql.Sql
import groovy.sql.GroovyRowResult
import au.org.ala.collectory.IdGeneratorService.IdType
import spock.lang.Specification

@Integration
class IdGeneratorServiceTests extends Specification {
    // Clean up our own mess. Nested transactions in H2 seem to lead to locking problems
    static transactional = false

    def idGeneratorService
    def sql

    def setup() {
        sql = new Sql(idGeneratorService.getDataSource())
        populate()
    }

    def cleanup() {
        sql.execute("delete from sequence")
    }

    def populate() {
        sql.execute("drop table if exists sequence")
        sql.execute("""
        create table if not exists sequence(
            id bigint primary key,
            version bigint not null,
            next_id bigint not null,
            name varchar(45) not null,
            prefix varchar(5) not null)
        """)
        // plant seeds
        def insertSeed = "insert into sequence(id, version, next_id, name, prefix) values (?,?,?,?,?)"
        sql.execute(insertSeed, [1, 1, 1, 'collection', 'co'])
        sql.execute(insertSeed, [2, 1, 1, 'institution', 'in'])
        sql.execute(insertSeed, [3, 1, 1, 'dataProvider', 'dp'])
        sql.execute(insertSeed, [4, 1, 1, 'dataResource', 'dr'])
        sql.execute(insertSeed, [5, 1, 1, 'dataHub', 'dh'])
        sql.execute(insertSeed, [6, 1, 1, 'attribution', 'at'])
    }

    def testGetNextId1() {
        when:
        def co1 = idGeneratorService.getNextCollectionId()
        def co2 = idGeneratorService.getNextCollectionId()
        def co3 = idGeneratorService.getNextCollectionId()
        def co4 = idGeneratorService.getNextCollectionId()
        then:
        co1 == "co1"
        co2 == "co2"
        co3 == "co3"
        co4 == "co4"
    }

    def testGetNextId2() {
        when:
        def in1 = idGeneratorService.getNextInstitutionId()
        def in2 = idGeneratorService.getNextInstitutionId()
        then:
        in1 == "in1"
        in2 == "in2"
    }

    def testGetNextId3() {
        when:
        def dh1 = idGeneratorService.getNextId(IdType.dataHub)
        def dh2 = idGeneratorService.getNextId(IdType.dataHub)
        then:
        dh1 == "dh1"
        dh2 == "dh2"
    }

}
