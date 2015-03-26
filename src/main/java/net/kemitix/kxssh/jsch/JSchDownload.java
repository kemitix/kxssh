package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshDownload;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;

public class JSchDownload extends JSchOperation implements SshDownload {

    public JSchDownload(SshConnectionProperties connectionProperties) {
        super(connectionProperties);
    }

    private static final String ERROR_FILE_LOCAL_OPEN = "Error opening local file for writing";
    private static final String ERROR_FILE_LOCAL_CLOSE = "Error closing local file";
    private static final String ERROR_ACK = "Error in ACK";

    @Override
    public void download(String remoteFilename, String localFilename) throws SshException {
        updateStatus(SshOperationStatus.STARTING);
        Session session = getSession();
        JSchIOChannel ioChannel = JSchIOChannel.createExecIOChannel(session);
        ioChannel.setRemoteFilename(remoteFilename);
        final File localFile = new File(localFilename);
        ioChannel.setLocalFile(localFile);

        updateStatus(SshOperationStatus.CONNECTING);
        ioChannel.connect();
        notifyRemoteReady(ioChannel);

        updateStatus(SshOperationStatus.DOWNLOADING);
        while (true) {
            int c = ioChannel.checkStatus();
            if (c != 'C') {
                break;
            }
            int filesize = readMetaData(ioChannel);
            notifyRemoteReady(ioChannel);
            try (FileOutputStream fos = new FileOutputStream(localFile)) {
                writeIOChannelToOutputStream(ioChannel, fos, filesize);
            } catch (FileNotFoundException ex) {
                updateStatus(SshErrorStatus.FILE_OPEN_ERROR);
                throw new SshException(ERROR_FILE_LOCAL_OPEN, ex);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.FILE_CLOSE_ERROR);
                throw new SshException(ERROR_FILE_LOCAL_CLOSE, ex);
            }
            if (ioChannel.checkStatus() != JSchIOChannel.SUCCESS) {
                updateStatus(SshErrorStatus.ACK_ERROR);
                throw new SshException(ERROR_ACK);
            }
            notifyRemoteReady(ioChannel);
        }

        updateStatus(SshOperationStatus.DISCONNECTING);
        session.disconnect();
        updateStatus(SshOperationStatus.DISCONNECTED);
    }

}
