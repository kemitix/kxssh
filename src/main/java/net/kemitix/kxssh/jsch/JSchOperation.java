package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.StatusListener;
import net.kemitix.kxssh.StatusProvider;

public class JSchOperation implements StatusProvider {

    private static final String SSHKNOWN_HOSTS = "~/.ssh/known_hosts";

    private final JSch jsch;

    protected final SshConnectionProperties connectionProperties;

    public JSchOperation(SshConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
        jsch = new JSch();
        try {
            jsch.setKnownHosts(SSHKNOWN_HOSTS);
        } catch (JSchException ex) {
            throw new RuntimeException(SSHKNOWN_HOSTS, ex);
        }
    }

    // SESSION
    private static final String ERROR_SESSION_HOST = "Error host not set";
    private static final String ERROR_SESSION = "Error creating/connecting session";

    protected Session getSession() throws SshException {
        SshAuthentication authentication = connectionProperties.getAuthentication();
        String hostname = connectionProperties.getHostname();
        String username = authentication.getUsername();

        if (hostname == null || hostname.equals("")) {
            updateStatus(SshErrorStatus.HOSTNAME_ERROR);
            throw new SshException(ERROR_SESSION_HOST);
        }
        if (username == null || username.equals("")) {
            username = System.getProperty("user.name");
        }

        Session session;
        try {
            session = jsch.getSession(username, hostname);
            session.setPassword(((SshPasswordAuthentication) authentication).getPassword().getBytes());
            session.connect();
        } catch (JSchException ex) {
            if (ex.getMessage().contains("UnknownHostKey")) {
                Logger.getLogger(this.getClass().getName())
                        .log(Level.SEVERE, "Try adding key with: ssh-keyscan -t rsa {0} >> {1}", new Object[]{
                            hostname, SSHKNOWN_HOSTS
                        });
            }
            updateStatus(SshErrorStatus.SESSION_ERROR);
            throw new SshException(ERROR_SESSION, ex);
        }
        return session;
    }

    //STREAM TO FILE
    private static final String ERROR_FILE_REMOTE_READ = "Error reading remote file";
    private static final String ERROR_FILE_LOCAL_WRITE = "Error writing local file";

    protected void writeIOChannelToOutputStream(
            JSchIOChannel ioChannel,
            FileOutputStream fos,
            int filesize)
            throws SshException {
        byte[] buf = new byte[1024];
        int remaining = filesize;
        /**
         * loop over buf.length sized blocks of input
         */
        while (true) {
            int bytesToRead;
            if (buf.length < remaining) {
                bytesToRead = buf.length;
            } else {
                bytesToRead = remaining;
            }
            int bytesRead;
            try {
                bytesRead = ioChannel.read(buf, 0, bytesToRead);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.CHANNEL_READ_ERROR);
                throw new SshException(ERROR_FILE_REMOTE_READ, ex);
            }
            if (bytesRead < 0) {
                // error
                break;
            }
            // prevent overrun
            if (bytesRead > remaining) {
                bytesRead = remaining;
            }
            try {
                fos.write(buf, 0, bytesRead);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.FILE_WRITE_ERROR);
                throw new SshException(ERROR_FILE_LOCAL_WRITE, ex);
            }
            remaining -= bytesRead;
            if (remaining == 0) {
                break;
            }
            if (remaining < 0) {
                updateStatus(SshErrorStatus.OVERRUN_ERROR);
                break;
            }
            updateProgress(filesize - remaining, filesize);
        }
        updateProgress(remaining < 0 ? filesize : filesize - remaining, filesize);
    }

    //STATUS LISTENER
    private StatusListener statusListener;

    @Override
    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void updateProgress(int progress, int total) {
        if (statusListener != null) {
            statusListener.onUpdateProgress(progress, total);
        }
    }

    @Override
    public void updateStatus(SshStatus status) {
        if (statusListener != null) {
            statusListener.onUpdateStatus(status);
        }
    }

    // METADATA
    private static final String ERROR_METADATA_READ = "Error reading initial metadata from input stream";

    protected int readMetaData(JSchIOChannel ioChannel) throws SshException {
        byte[] buf = new byte[1024];
        int filesize = 0;
        try {
            // read file unix permissions, 4 chars with a space terminator
            // e.g. '0644 '
            ioChannel.read(buf, 0, 5);

            // read filesize as a string, terminated by a null or space
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (ioChannel.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }
                if (buf[0] == ' ') {
                    break;
                }
                sb.append(buf[0] - '0');
            }
            filesize = Integer.parseInt(sb.toString());

            // read filename (?), terminated by 0x0a
            String file = null;
            for (int i = 0;; i++) {
                ioChannel.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }
        } catch (IOException ex) {
            updateStatus(SshErrorStatus.METADATA_READ_ERROR);
            throw new SshException(ERROR_METADATA_READ, ex);
        }
        return filesize;
    }

    private static final String ERROR_REMOTE_NOTIFY = "Error writing/flushing null on output stream";

    protected void notifyRemoteReady(JSchIOChannel ioChannel) throws SshException {
        byte[] buf = new byte[1];
        // send '\0'
        buf[0] = 0;
        try {
            ioChannel.write(buf, 0, 1);
            ioChannel.flush();
        } catch (IOException ex) {
            updateStatus(SshErrorStatus.REMOTE_NOTIFY_ERROR);
            throw new SshException(ERROR_REMOTE_NOTIFY, ex);
        }
    }

}
