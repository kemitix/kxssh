package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.SftpClient;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshIOFactory;
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
    private JSchScpDownload download;
    private JSchScpUpload upload;

    /**
     * Constructor.
     *
     * @param connectionProps the remote host and authentication details
     */
    public JSchSftpClient(final SshConnectionProperties connectionProps) {
        connectionProperties = connectionProps;
    }

    @Override
    public void download(final String remoteFilename, final File localFile) {
        requireDownload();
        download.download(remoteFilename, localFile);
    }

    /**
     * Creates the {@link ScpDownload} implementation.
     */
    protected void requireDownload() {
        if (download == null) {
            download = new JSchScpDownload(connectionProperties,
                    new JSchFactory(), new SshIOFactory());
            download.setStatusListener(statusListener);
        }
    }

    @Override
    public void upload(final File localFile, final String remoteFilename) {
        requireUpload();
        upload.upload(localFile, remoteFilename);
    }

    /**
     * Creates the {@link ScpUpload} implementation.
     */
    protected void requireUpload() {
        if (upload == null) {
            upload = new JSchScpUpload(connectionProperties,
                    new JSchFactory(), new SshIOFactory());
            upload.setStatusListener(statusListener);
        }
    }

    //STATUS LISTENER
    private SshStatusListener statusListener;

    /**
     * Set the listener to respond to status updates.
     *
     * @param listener the status listener
     */
    public void setStatusListener(final SshStatusListener listener) {
        statusListener = listener;
    }

    /**
     * Notify the listener that the progress has updated.
     *
     * @param progress the current progress position
     * @param total    the total that the progress is moving toward
     */
    public void updateProgress(final long progress, final long total) {
        statusListener.onUpdateProgress(progress, total);
    }

    /**
     * Notify the listener that the status has updated.
     *
     * @param status the updated status
     */
    public void updateStatus(final SshStatus status) {
        statusListener.onUpdateStatus(status);
    }

}
