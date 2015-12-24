package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.ScpDownload;
import net.kemitix.kxssh.ScpUpload;
import net.kemitix.kxssh.SftpClient;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.SshStatusListener;

import lombok.Setter;

import java.io.File;

/**
 * Implementation of an SFTP client using JSCH.
 *
 * @author pcampbell
 */
@Setter
public class JSchSftpClient implements SftpClient {

    private final SshConnectionProperties connectionProperties;
    private ScpDownload download;
    private ScpUpload upload;

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
    public void upload(File localFile, String remoteFilename) throws SshException {
        requireUpload();
        upload.upload(localFile, remoteFilename);
    }

    protected void requireUpload() {
        if (upload == null) {
            upload = new JSchScpUpload(connectionProperties);
            upload.setStatusListener(statusListener);
        }
    }

    //STATUS LISTENER
    private SshStatusListener statusListener;

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
