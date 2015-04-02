package net.kemitix.kxssh.jsch;

import java.io.File;
import lombok.Setter;
import net.kemitix.kxssh.SftpClient;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.ScpDownload;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.SshStatusListener;

@Setter
public class JSchSftpClient implements SftpClient {

    private final SshConnectionProperties connectionProperties;
    private ScpDownload download;

    private SshStatusListener statusListener;

    public JSchSftpClient(SshConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    @Override
    public void download(String remoteFilename, File localFile) throws SshException {
        requireDownload();
        download.download(remoteFilename, localFile);
    }

    protected void requireDownload() {
        if (download == null) {
            download = new JSchScpDownload(connectionProperties);
            download.setStatusListener(statusListener);
        }
    }

    @Override
    public void setStatusListener(SshStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void updateProgress(long progress, long total) {
        statusListener.onUpdateProgress(progress, total);
    }

    @Override
    public void updateStatus(SshStatus status) {
        statusListener.onUpdateStatus(status);
    }

}
