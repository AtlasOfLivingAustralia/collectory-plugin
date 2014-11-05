package au.org.ala.collectory.exception

/**
 * Created by markew
 * Date: Sep 9, 2010
 * Time: 1:50:31 PM
 */
class InvalidUidException extends RuntimeException {

    def InvalidUidException(String message) {
        super(message);
    }

    def InvalidUidException(String message, Throwable cause) {
        super(message, cause);
    }

}
