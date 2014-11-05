package au.org.ala.collectory

/**
 * Created by markew
 * Date: Jul 1, 2010
 * Time: 8:52:46 AM
 */
class CollectionLocation {
    BigDecimal latitude = -1
    BigDecimal longitude = -1
    String streetAddress
    String name
    String link

    boolean isEmpty() {
        if (latitude != 0 && latitude != -1 && longitude != 0 && longitude != -1)
            return false
        if (streetAddress)
            return false
        return true
    }
}

