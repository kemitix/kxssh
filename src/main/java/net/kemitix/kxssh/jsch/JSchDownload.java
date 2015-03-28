package net.kemitix.kxssh.jsch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import lombok.Setter;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshDownload;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;

@Setter
public class JSchDownload extends JSchOperation implements SshDownload {

    public JSchDownload(SshConnectionProperties connectionProperties) {
        super(connectionProperties);
    }

    private static final String ERROR_FILE_LOCAL_OPEN = "Error opening local file for writing";
    private static final String ERROR_ACK = "Error in ACK";

    @Override
    public void download(String remoteFilename, File localFile) throws SshException {
        updateStatus(SshOperationStatus.STARTING);

        JSchIOChannel ioChannel = getExecIOChannel();
        ioChannel.setRemoteFilename(remoteFilename);
        ioChannel.setLocalFile(localFile);

        updateStatus(SshOperationStatus.CONNECTING);

        ioChannel.connect();
        ioChannel.notifyReady();

        updateStatus(SshOperationStatus.DOWNLOADING);

        while (true) {
            if (ioChannel.checkStatus() != JSchIOChannel.CONTINUE) {
                break;
            }
            IOChannelMetadata metadata = ioChannel.readMetaData();
            ioChannel.notifyReady();

            OutputStream stream = getOutputStream(localFile);
            writeIOChannelToOutputStream(ioChannel, stream, metadata.getFilesize());
            if (ioChannel.checkStatus() != JSchIOChannel.SUCCESS) {
                updateStatus(SshErrorStatus.ACK_ERROR);
                throw new SshException(ERROR_ACK);
            }
            ioChannel.notifyReady();
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
