package net.kemitix.kxssh;

/**
 * Enumerations of normal operation statuses for an SSH connection.
 *
 * @author pcampbell
 */
public enum SshOperationStatus implements SshStatus {

    STARTING,
    CONNECTING,
    CONNECTED,
    DOWNLOADING,
    UPLOADING,
    DISCONNECTING,
    DISCONNECTED;

}
