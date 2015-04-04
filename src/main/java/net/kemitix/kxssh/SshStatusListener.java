package net.kemitix.kxssh;

public interface SshStatusListener {

    void onUpdateProgress(long remaining, long filesize);

    void onUpdateStatus(SshStatus status);

}
