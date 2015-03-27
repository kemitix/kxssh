package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Setter;
import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.StatusListener;
import net.kemitix.kxssh.StatusProvider;

@Setter
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

    protected Session session;

    protected void initSession() throws SshException {
        if (session != null) {
            return;
        }
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

        try {
            session = jsch.getSession(username, hostname);
            session.setPassword(((SshPasswordAuthentication) authentication).getPassword());
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
    }

    protected void disconnect() {
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    //STREAM TO FILE
    private static final String ERROR_FILE_LOCAL_WRITE = "Error writing local file";

    protected void writeIOChannelToOutputStream(
            JSchIOChannel ioChannel,
            FileOutputStream stream,
            int filesize)
            throws SshException {
        byte[] buffer = new byte[1024];
        int remaining = filesize;
        // loop over buffer.length sized blocks of input
        do {
            int bytesToRead = Integer.min(buffer.length, remaining);
            int bytesRead = ioChannel.read(buffer, 0, bytesToRead);
            // prevent overrun
            bytesRead = Integer.min(bytesRead, bytesToRead);
            try {
                stream.write(buffer, 0, bytesRead);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.FILE_WRITE_ERROR);
                throw new SshException(ERROR_FILE_LOCAL_WRITE, ex);
            }
            remaining -= bytesRead;
            updateProgress(filesize - remaining, filesize);
        } while (remaining > 0);
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

}
