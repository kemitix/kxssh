package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.ScpDownload;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.scp.ScpCommand;
import net.kemitix.kxssh.scp.ScpCopyCommand;

import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of the SCP Download operation using JCSH.
 *
 * @author pcampbell
 */
@Setter
public class JSchScpDownload extends JSchScpOperation implements ScpDownload {

    /**
     * Constructor.
     *
     * @param connectionProperties the remote host and authentication details
     */
    public JSchScpDownload(SshConnectionProperties connectionProperties) {
        super(connectionProperties);
    }

    private static final String ERROR_FILE_LOCAL_OPEN = "Error opening local file for writing";
    private static final String ERROR_ACK = "Error in ACK";

    @Override
    public void download(String remoteFilename, File localFile) {
        updateStatus(SshOperationStatus.STARTING);
        JSchIOChannel ioChannel = getExecIOChannel();
        updateStatus(SshOperationStatus.DOWNLOADING);

        // scp "from"
        ioChannel.setExecCommand("scp -f " + remoteFilename);
        ioChannel.setLocalFile(localFile);
        ioChannel.connect();
        ioChannel.notifyReady();

        do {
            ScpCopyCommand scpCopyCommand = readScpCopyCommand(ioChannel);
            ioChannel.notifyReady();
            OutputStream stream = getOutputStream(localFile);
            ioChannel.writeToStream(stream, scpCopyCommand.getLength());
            if (ioChannel.checkStatus() != JSchIOChannel.SUCCESS) {
                updateStatus(SshErrorStatus.ACK_ERROR);
                throw new SshException(ERROR_ACK);
            }
            ioChannel.notifyReady();
        } while (ioChannel.checkStatus() == JSchIOChannel.CONTINUE);

        updateStatus(SshOperationStatus.DISCONNECTING);
        disconnect();
        updateStatus(SshOperationStatus.DISCONNECTED);
    }

    /**
     * Read an SCP Copy command header from the remote server.
     *
     * @param ioChannel the channel to read the command from
     *
     * @return the SCP Copy command
     */
    private ScpCopyCommand readScpCopyCommand(JSchIOChannel ioChannel) {
        try {
            ScpCommand scpCommand = ioChannel.readScpCommand();
            if (!(scpCommand instanceof ScpCopyCommand)) {
                throw new SshException("Unexpected SCP protocol command (only support single files)");
            }
            return (ScpCopyCommand) scpCommand;
        } catch (IOException ex) {
            throw new SshException("Error reading SCP protocol command", ex);
        }
    }

    /**
     * Creates a {@link FileOutputStream} for the local file.
     *
     * @param localFile the file the stream should write to
     *
     * @return the output stream
     */
    private FileOutputStream getOutputStream(File localFile) {
        try {
            return ioFactory.createFileOutputStream(localFile);
        } catch (FileNotFoundException ex) {
            updateStatus(SshErrorStatus.FILE_OPEN_ERROR);
            throw new SshException(ERROR_FILE_LOCAL_OPEN, ex);
        }
    }

}
