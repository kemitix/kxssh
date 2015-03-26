package net.kemitix.kxssh;

public interface StatusListener {

    void onUpdateProgress(int remaining, int filesize);

    void onUpdateStatus(String message);

}
