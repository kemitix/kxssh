package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.ScpUpload;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.scp.ScpCopyCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the SCP Upload operation using JCSH.
 *
 * @author pcampbell
 */
class JSchScpUpload extends JSchScpOperation implements ScpUpload {

    private static final String ERROR_FILE_LOCAL_OPEN
            = "Error opening local file for writing";

    /**
     * Constructor.
     *
     * @param connectionProperties the remote host and authentication details
     */
    JSchScpUpload(final SshConnectionProperties connectionProperties) {
        super(connectionProperties);
    }

    @Override
    public void upload(final File localFile, final String remoteFilename) {
        byte[] fileMode = new byte[4];
        fileMode[0] = '0';
        fileMode[1] = '6';
        fileMode[2] = '4';
        fileMode[3] = '0';
        upload(localFile, remoteFilename, fileMode);
    }

    /**
     * Uploads the local file to the remote server.
     *
     * @param localFile      the local file to be uploaded
     * @param remoteFilename the file name on the remote server to upload to
     * @param fileMode       the Unix file permissions to be set on the uploaded
     *                       file
     */
    public void upload(
            final File localFile,
            final String remoteFilename,
            final byte[] fileMode) {
        updateStatus(SshOperationStatus.STARTING);
        JSchIOChannel ioChannel = getExecIOChannel();

        // scp "to"
        ioChannel.setExecCommand("scp -t " + remoteFilename);
        ioChannel.setLocalFile(localFile);
        ioChannel.connect();

        if (ioChannel.checkStatus() == JSchIOChannel.SUCCESS) {
            updateStatus(SshOperationStatus.UPLOADING);
            ScpCopyCommand scpCopyCommand = new ScpCopyCommand();
            scpCopyCommand.setFileMode(fileMode);
            long filesize = localFile.length();
            scpCopyCommand.setLength(filesize);
            scpCopyCommand.setName(localFile.getName());
            try {
                byte[] command = scpCopyCommand.getBytes();
                ioChannel.write(command, 0, command.length);
            } catch (IOException ex) {
                throw new SshException("Error writing scp command", ex);
            }

            InputStream stream = getInputStream(localFile);
            try {
                ioChannel.readFromStream(stream, filesize);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.FILE_OPEN_ERROR);
                throw new SshException("Error reading file", ex);
            }
        }
        updateStatus(SshOperationStatus.DISCONNECTING);
        disconnect();
        updateStatus(SshOperationStatus.DISCONNECTED);
    }

    /**
     * Creates an {@link InputStream} for the local file.
     *
     * @param localFile the file the stream should read from
     *
     * @return the input stream
     */
    private InputStream getInputStream(final File localFile) {
        try {
            return ioFactory.createFileInputStream(localFile);
        } catch (FileNotFoundException ex) {
            updateStatus(SshErrorStatus.FILE_OPEN_ERROR);
            throw new SshException(ERROR_FILE_LOCAL_OPEN, ex);
        }
    }

}
