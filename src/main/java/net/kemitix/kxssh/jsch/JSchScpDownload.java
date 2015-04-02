package net.kemitix.kxssh.jsch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.Setter;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.ScpDownload;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.scp.ScpCommand;
import net.kemitix.kxssh.scp.ScpCopyCommand;

@Setter
public class JSchScpDownload extends JSchScpOperation implements ScpDownload {

    public JSchScpDownload(SshConnectionProperties connectionProperties) {
        super(connectionProperties);
    }

    private static final String ERROR_FILE_LOCAL_OPEN = "Error opening local file for writing";
    private static final String ERROR_ACK = "Error in ACK";

    @Override
    public void download(String remoteFilename, File localFile) throws SshException {
        updateStatus(SshOperationStatus.STARTING);

        JSchIOChannel ioChannel = getExecIOChannel();

        updateStatus(SshOperationStatus.DOWNLOADING);

        // scp "from"
        ioChannel.setExecCommand("scp -f " + remoteFilename);
        ioChannel.setLocalFile(localFile);

        ioChannel.connect();
        ioChannel.notifyReady();

        while (true) {
            ScpCommand scpCommand;
            try {
                scpCommand = ioChannel.readScpCommand();
            } catch (IOException ex) {
                throw new SshException("Error reading SCP protocol command", ex);
            }
            if (!(scpCommand instanceof ScpCopyCommand)) {
                throw new SshException("Unexpected SCP protocol command (only support single files)");
            }
            ScpCopyCommand scpCopyCommand = (ScpCopyCommand) scpCommand;

            ioChannel.notifyReady();

            OutputStream stream = getOutputStream(localFile);
            writeIOChannelToOutputStream(ioChannel, stream, scpCopyCommand.getLength());
            if (ioChannel.checkStatus() != JSchIOChannel.SUCCESS) {
                updateStatus(SshErrorStatus.ACK_ERROR);
                throw new SshException(ERROR_ACK);
            }
            ioChannel.notifyReady();
            if (ioChannel.checkStatus() != JSchIOChannel.CONTINUE) {
                break;
            }
        }

        updateStatus(SshOperationStatus.DISCONNECTING);

        releaseIOChannel();
        disconnect();

        updateStatus(SshOperationStatus.DISCONNECTED);
    }

    private FileOutputStream getOutputStream(File localFile) throws SshException {
        try {
            return ioFactory.createFileOutputStream(localFile);
        } catch (FileNotFoundException ex) {
            updateStatus(SshErrorStatus.FILE_OPEN_ERROR);
            throw new SshException(ERROR_FILE_LOCAL_OPEN, ex);
        }
    }

}
