package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.SshDownload;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshException;

public class JSchDownload extends JSchOperation implements SshDownload {

    public JSchDownload(SshConnectionProperties connectionProperties) {
        super(connectionProperties);
    }

    private static final String ERROR_FILE_LOCAL_OPEN = "Error opening local file for writing";
    private static final String ERROR_FILE_LOCAL_CLOSE = "Error closing local file";
    private static final String ERROR_ACK = "Error in ACK";

    @Override
    public void download(String remoteFilename, String localFilename) throws SshException {
        updateStatus("Starting Session...");
        Session session = getSession();
        JSchIOChannel ioChannel = JSchIOChannel.createExecIOChannel(session);
        ioChannel.setRemoteFilename(remoteFilename);
        final File localFile = new File(localFilename);
        ioChannel.setLocalFile(localFile);
        updateStatus("Connecting...");
        ioChannel.connect();
        notifyRemoteReady(ioChannel);
        updateStatus("Downloading...");
        while (true) {
            int c = checkAck(ioChannel);
            if (c != 'C') {
                break;
            }
            int filesize = readMetaData(ioChannel);
            notifyRemoteReady(ioChannel);
            try {
                FileOutputStream fos = new FileOutputStream(localFile);
                writeIOChannelToOutputStream(ioChannel, fos, filesize);
                fos.close();
            } catch (FileNotFoundException ex) {
                updateStatus(ERROR_FILE_LOCAL_OPEN);
                throw new SshException(ERROR_FILE_LOCAL_OPEN, ex);
            } catch (IOException ex) {
                updateStatus(ERROR_FILE_LOCAL_CLOSE);
                throw new SshException(ERROR_FILE_LOCAL_CLOSE, ex);
            }
            if (checkAck(ioChannel) != 0) {
                updateStatus(ERROR_ACK);
                throw new SshException(ERROR_ACK);
            }
            notifyRemoteReady(ioChannel);
        }
        updateStatus("Disconnecting...");
        session.disconnect();
        updateStatus("Downloaded");
    }

}
