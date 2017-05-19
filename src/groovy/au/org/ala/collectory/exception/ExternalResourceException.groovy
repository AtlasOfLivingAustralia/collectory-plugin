package au.org.ala.collectory.exception

/**
 * An exception raised when there is a problem with an external resource
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class ExternalResourceException extends Exception {
    String code
    Object[] args

    ExternalResourceException() {
        super()
    }

    ExternalResourceException(String message, String code, Object... args) {
        super(message)
        this.code = code
        this.args = args
    }

    ExternalResourceException(String message, Throwable cause, String code, Object... args) {
        super(message, cause)
        this.code = code
        this.args = args
    }
}
