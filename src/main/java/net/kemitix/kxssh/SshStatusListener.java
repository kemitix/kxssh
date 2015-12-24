package net.kemitix.kxssh;

/**
 * Interface for receiving the status of an SSH connection.
 *
 * @author pcampbell
 */
public interface SshStatusListener {

    void onUpdateProgress(long remaining, long filesize);

    void onUpdateStatus(SshStatus status);

}
