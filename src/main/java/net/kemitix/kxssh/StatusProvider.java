package net.kemitix.kxssh;

public interface StatusProvider {

    void setStatusListener(StatusListener statusListener);

    void updateProgress(long progress, long total);

    void updateStatus(SshStatus status);

}
