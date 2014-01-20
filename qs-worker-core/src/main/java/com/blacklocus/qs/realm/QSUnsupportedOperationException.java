package com.blacklocus.qs.realm;

/**
 * Thrown by {@link QSInfoService} methods that do not (perhaps cannot) be supported by their backing store. The realm
 * application should be tolerant of such unsupported methods.
 *
 * @author Jason Dunkelberger (dirkraft)
 */
public class QSUnsupportedOperationException extends UnsupportedOperationException {

    public QSUnsupportedOperationException() {
    }

    public QSUnsupportedOperationException(String message) {
        super(message);
    }

    public QSUnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public QSUnsupportedOperationException(Throwable cause) {
        super(cause);
    }

}
