package net.kemitix.kxssh;

public enum SshOperationStatus implements SshStatus {

    STARTING,
    CONNECTING,
    CONNECTED,
    DOWNLOADING,
    DISCONNECTING,
    DISCONNECTED;

}
