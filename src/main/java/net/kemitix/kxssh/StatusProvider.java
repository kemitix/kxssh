package net.kemitix.kxssh;

public interface StatusProvider {

    void setStatusListener(StatusListener statusListener);

    void updateProgress(int progress, int total);

    void updateStatus(SshStatus status);

}
