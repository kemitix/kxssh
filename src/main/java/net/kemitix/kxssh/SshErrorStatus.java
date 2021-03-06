package net.kemitix.kxssh;

public enum SshErrorStatus implements SshStatus {

    HOSTNAME_ERROR,
    SESSION_ERROR,
    FILE_OPEN_ERROR,
    CHANNEL_READ_ERROR,
    METADATA_READ_ERROR,
    REMOTE_NOTIFY_ERROR,
    OVERRUN_ERROR,
    FILE_WRITE_ERROR,
    FILE_CLOSE_ERROR,
    ACK_ERROR,
    ACK_FATAL_ERROR,
    ACK_READ_ERROR;

}
