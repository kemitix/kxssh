package net.kemitix.kxssh.jsch;

import java.io.File;
import lombok.Setter;
import net.kemitix.kxssh.SftpClient;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshDownload;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.StatusListener;

@Setter
public class JSchSftpClient implements SftpClient {

    private final SshConnectionProperties connectionProperties;
    private SshDownload download;

    private StatusListener statusListener;

    public JSchSftpClient(SshConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    @Override
    public void download(String remoteFilename, File localFile) throws SshException {
        if (download == null) {
            download = new JSchDownload(connectionProperties);
            download.setStatusListener(statusListener);
        }
        download.download(remoteFilename, localFile);
    }

    @Override
    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void updateProgress(int progress, int total) {
        statusListener.onUpdateProgress(progress, total);
    }

    @Override
    public void updateStatus(SshStatus status) {
        statusListener.onUpdateStatus(status);
    }

}
