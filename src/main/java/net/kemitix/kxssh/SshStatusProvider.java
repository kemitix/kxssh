package net.kemitix.kxssh;

/**
 * Interface for dispatching SSH Status updates.
 *
 * @author pcampbell
 */
public interface SshStatusProvider {

    void setStatusListener(SshStatusListener statusListener);

    void updateProgress(long progress, long total);

    void updateStatus(SshStatus status);

}
