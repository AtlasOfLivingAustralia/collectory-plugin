package au.org.ala.collectory

class Address {

    static final long serialVersionUID = 1L;//1681261914339207268L;

    String street           // includes number eg 186 Tinaroo Creek Road
    String postBox          // eg PO Box 2104
    String city
    String state            // full name eg Queensland
    String postcode
    String country

    static transients = ['empty']

    static constraints = {
        street(nullable:true)
        postBox(nullable:true)
        city(nullable:true)
        state(nullable:true)
        postcode(nullable:true)
        country(nullable:true)
    }

    def isEmpty() {
        return [street, postBox, city, state, postcode, country].every {!it}
        //return !(street || postBox || city || state || postcode || country)
    }

    List<String> nonEmptyAddressElements(includePostal) {
        def fields = ['street','city','state','postcode']
        if (includePostal) {fields << 'postBox'}
        List<String> elements = []
        fields.each {
            if (this."${it}") {
                elements << this."${it}"
            }
        }
        return elements
    }

    String buildAddress() {
        return nonEmptyAddressElements(false).join(", ")
    }

    def String toString() {
        return nonEmptyAddressElements(true).join(", ")
    }

    def boolean equals(Object obj) {
        return obj instanceof Address &&
                street == obj.street &&
                postBox == obj.postBox &&
                city == obj.city &&
                state == obj.state &&
                postcode == obj.postcode &&
                country == obj.country
    }
}
