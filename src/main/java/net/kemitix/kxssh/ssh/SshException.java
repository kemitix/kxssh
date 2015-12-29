package net.kemitix.kxssh.ssh;

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
    public SshException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the message
     * @param cause   the original cause
     */
    public SshException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
