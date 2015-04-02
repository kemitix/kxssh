package net.kemitix.kxssh;

public interface StatusListener {

    void onUpdateProgress(long remaining, long filesize);

    void onUpdateStatus(SshStatus status);

}
