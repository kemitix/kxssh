package net.kemitix.kxssh.ssh;

/**
 * Interface for dispatching SSH Status updates.
 *
 * @author pcampbell
 */
public interface SshStatusProvider {

    /**
     * Set the listener that will receive the status updates.
     *
     * @param statusListener the listener to receive updates
     */
    void setStatusListener(SshStatusListener statusListener);

    /**
     * Send a notification to the status listener of the progress.
     *
     * @param progress the current progress value
     * @param total    the expected final value
     */
    void updateProgress(long progress, long total);

    /**
     * Send a notification to the status listener of the status.
     *
     * @param status the status to send
     */
    void updateStatus(SshStatus status);

}
