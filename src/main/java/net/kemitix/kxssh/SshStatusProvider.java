package net.kemitix.kxssh;

public interface SshStatusProvider {

    void setStatusListener(SshStatusListener statusListener);

    void updateProgress(long progress, long total);

    void updateStatus(SshStatus status);

}
