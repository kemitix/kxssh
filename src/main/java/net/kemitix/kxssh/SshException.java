package net.kemitix.kxssh;

/**
 * Represents an exception thrown by the kxssh library.
 *
 * @author pcampbell
 */
public class SshException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message the message
     */
    public SshException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the message
     * @param cause   the original cause
     */
    public SshException(String message, Throwable cause) {
        super(message, cause);
    }

}
