package net.kemitix.kxssh.jsch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.kemitix.kxssh.ScpUpload;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.scp.ScpCopyCommand;

class JSchScpUpload extends JSchScpOperation implements ScpUpload {

    private static final String ERROR_FILE_LOCAL_OPEN = "Error opening local file for writing";

    public JSchScpUpload(SshConnectionProperties connectionProperties) {
        super(connectionProperties);
    }

    @Override
    public void upload(File localFile, String remoteFilename) throws SshException {
        byte[] fileMode = new byte[4];
        fileMode[0] = '0';
        fileMode[1] = '6';
        fileMode[2] = '4';
        fileMode[3] = '0';
        upload(localFile, remoteFilename, fileMode);
    }

    public void upload(File localFile, String remoteFilename, byte[] fileMode) throws SshException {
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

    private InputStream getInputStream(File localFile) throws SshException {
        try {
            return ioFactory.createFileInputStream(localFile);
        } catch (FileNotFoundException ex) {
            updateStatus(SshErrorStatus.FILE_OPEN_ERROR);
            throw new SshException(ERROR_FILE_LOCAL_OPEN, ex);
        }
    }

}
