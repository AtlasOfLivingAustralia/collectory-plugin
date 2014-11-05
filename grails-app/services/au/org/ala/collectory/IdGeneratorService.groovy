package au.org.ala.collectory
import groovy.sql.Sql
import javax.sql.DataSource
import groovy.sql.GroovyRowResult
import org.springframework.transaction.annotation.Transactional

class IdGeneratorService implements Serializable {

    static transactional = true
    javax.sql.DataSource dataSource

    public enum IdType {
        collection('co'),
        institution('in'),
        dataProvider('dp'),
        dataResource('dr'),
        dataHub('dh'),
        attribution('at'),
        tempDataResource('drt')

        public String prefix
        public IdType(prefix) {
            this.prefix = prefix
        }

        public static IdType lookup(pf) {
            return IdType.values().find {it.prefix == pf} as IdType
        }

        public getIndex() {
            return ordinal() + 1
        }
    };

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    def getNextId(IdType type) {
        def sql = new Sql(dataSource)
        def id = type.getIndex()

        GroovyRowResult row = sql.firstRow("select next_id,prefix from sequence where id = ?",[id]) as GroovyRowResult

        long next = row.next_id
        String prefix = row.prefix

        sql.executeUpdate("update sequence set next_id = ? where id = ?",[next + 1,id])

        return prefix + next
    }

    /**
     *
     * @param uid
     * @return
     * @deprecated uids are all generated internally so this check is no longer required
     */
    def checkValidId(String uid) {
        if (!uid || uid.size() < 3) {return false}
        IdType type = IdType.lookup(uid.startsWith('drt')?'drt':uid[0..1])
        if (!type) {return false}
        def id = type.getIndex()
        def sql = new Sql(dataSource)
        GroovyRowResult row = sql.firstRow("select next_id from sequence where id = ?",[id]) as GroovyRowResult
        long next = row.next_id
        String uidValueStr = uid.startsWith('drt')?'drt':uid[0..1]
        long uidValue
        try {
            uidValue = Long.parseLong(uidValueStr)
        } catch (NumberFormatException e) {
            return false
        }
        return uidValue < next
    }

    /* for tests */
    DataSource getDataSource() {
        return dataSource
    }

    /* convenience methods */
    def getNextCollectionId() {
        getNextId(IdType.collection)
    }
    def getNextInstitutionId() {
        getNextId(IdType.institution)
    }
    def getNextDataProviderId() {
        getNextId(IdType.dataProvider)
    }
    def getNextDataResourceId() {
        getNextId(IdType.dataResource)
    }
    def getNextDataHubId() {
        getNextId(IdType.dataHub)
    }
    def getNextAttributionId() {
        getNextId(IdType.attribution)
    }
    def getNextTempDataResource() {
        getNextId(IdType.tempDataResource)
    }
}
