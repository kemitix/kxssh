package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Setter;
import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshIOFactory;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.StatusListener;
import net.kemitix.kxssh.StatusProvider;

@Setter
public abstract class JSchOperation implements StatusProvider {

    private String knownHosts = "~/.ssh/known_hosts";

    protected final SshConnectionProperties connectionProperties;

    protected SshIOFactory ioFactory;

    private JSchFactory jschFactory;

    public JSchOperation(SshConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
        jschFactory = new JSchFactory();
        ioFactory = new SshIOFactory();
    }

    protected JSch getJSch() {
        try {
            return jschFactory.build(knownHosts);
        } catch (JSchException ex) {
            throw new RuntimeException(knownHosts, ex);
        }
    }

    // IOCHANNEL
    private JSchIOChannel ioChannel;

    protected JSchIOChannel getExecIOChannel() throws SshException {
        if (ioChannel == null) {
            initSession();
            ioChannel = JSchIOChannel.createExecIOChannel(session);
            ioChannel.connect();
            ioChannel.notifyReady();
        }
        return ioChannel;
    }

    protected void releaseIOChannel() {
        if (ioChannel != null) {
            ioChannel.disconnect();
            ioChannel = null;
        }
    }

    // SESSION
    private static final String ERROR_SESSION_HOST = "Error host not set";
    private static final String ERROR_SESSION = "Error creating/connecting session";

    private Session session;

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
            session = getJSch().getSession(username, hostname);
            session.setPassword(((SshPasswordAuthentication) authentication).getPassword());
            session.connect();
        } catch (JSchException ex) {
            if (ex.getMessage().contains("UnknownHostKey")) {
                Logger.getLogger(this.getClass().getName())
                        .log(Level.SEVERE, "Try adding key with: ssh-keyscan -t rsa {0} >> {1}", new Object[]{
                            hostname, knownHosts
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
            OutputStream stream,
            int filesize)
            throws SshException {
        int blockSize = 1024;
        int remaining = filesize;
        updateProgress(0, filesize);
        // loop over buffer.length sized blocks of input
        do {
            int bytesToRead = Integer.min(blockSize, remaining);
            IOChannelReadReply channelReadReply = ioChannel.read(bytesToRead);
            int bytesRead = channelReadReply.getBytesRead();
            // prevent overrun
            bytesRead = Integer.min(bytesRead, bytesToRead);
            try {
                stream.write(channelReadReply.getBuffer(), 0, bytesRead);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.FILE_WRITE_ERROR);
                throw new SshException(ERROR_FILE_LOCAL_WRITE, ex);
            }
            remaining -= bytesRead;
            updateProgress(filesize - remaining, filesize);
        } while (remaining > 0);
        updateProgress(filesize, filesize);
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
