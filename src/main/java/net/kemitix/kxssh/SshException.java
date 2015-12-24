package net.kemitix.kxssh;

/**
 * Represents an exception thrown by the kxssh library.
 *
 * @author pcampbell
 */
public class SshException extends Exception {

    public SshException(String message) {
        super(message);
    }

    public SshException(String message, Throwable cause) {
        super(message, cause);
    }

}
