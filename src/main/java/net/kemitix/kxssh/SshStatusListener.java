package net.kemitix.kxssh;

/**
 * Interface for receiving the status of an SSH connection.
 *
 * @author pcampbell
 */
public interface SshStatusListener {

    /**
     * Called when a {@link net.kemitix.kxssh.SshStatusProvider} updates the
     * progress.
     *
     * @param remaining the number of bytes still to be transfered
     * @param filesize  the number of bytes to be transfered in total
     */
    void onUpdateProgress(long remaining, long filesize);

    /**
     * Called when a {@link net.kemitix.kxssh.SshStatusProvider} updates the
     * status.
     *
     * @param status the updated status
     */
    void onUpdateStatus(SshStatus status);

}
