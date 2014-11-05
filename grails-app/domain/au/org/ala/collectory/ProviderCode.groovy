package au.org.ala.collectory

class ProviderCode {

    String code

    static auditable = [ignore: ['version']]

    static belongsTo = ProviderMap

    static mapping = {
        sort: 'code'
    }

    static constraints = {
        code(maxSize: 200, blank:false)  // should be unique:true but there seems to be a bug in case sensitivity
    }

    String toString() {
        return code
    }

}
