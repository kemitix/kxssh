package net.kemitix.kxssh;

public class SshException extends Exception {

    public SshException(String message) {
        super(message);
    }

    public SshException(String message, Throwable cause) {
        super(message, cause);
    }

}
