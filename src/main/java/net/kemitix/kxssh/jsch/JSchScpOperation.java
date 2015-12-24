package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshIOFactory;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.SshStatusListener;
import net.kemitix.kxssh.SshStatusProvider;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Setter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class representing an SCP operation using JSCH.
 *
 * @author pcampbell
 */
@Setter
public abstract class JSchScpOperation implements SshStatusProvider {

    private String knownHosts = "~/.ssh/known_hosts";

    protected final SshConnectionProperties connectionProperties;

    protected SshIOFactory ioFactory;

    private JSchFactory jschFactory;

    public JSchScpOperation(SshConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
        jschFactory = new JSchFactory();
        ioFactory = new SshIOFactory();
    }

    protected JSch getJSch(SshAuthentication authentication) throws SshException {
        try {
            return jschFactory
                    .knownHosts(knownHosts)
                    .authenticate(authentication)
                    .build();
        } catch (JSchException ex) {
            throw new RuntimeException(knownHosts, ex);
        }
    }

    // IOCHANNEL
    private JSchIOChannel ioChannel;

    protected JSchIOChannel getExecIOChannel() throws SshException {
        if (ioChannel == null) {
            ioChannel = JSchIOChannel.createExecIOChannel(getSession());
            ioChannel.setStatusListener(statusListener);
        }
        return ioChannel;
    }

    protected void disconnect() {
        if (ioChannel != null) {
            ioChannel.disconnect();
            ioChannel = null;
        }
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    // SESSION
    private static final String ERROR_SESSION_HOST = "Error host not set";
    private static final String ERROR_SESSION = "Error creating/connecting session";

    private Session session;

    protected Session getSession() throws SshException {
        if (session != null) {
            return session;
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
            session = getJSch(authentication).getSession(username, hostname);
            authentication.authenticateSession(session);
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
        return session;
    }

    //STATUS LISTENER
    private SshStatusListener statusListener;

    @Override
    public void setStatusListener(SshStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void updateProgress(long progress, long total) {
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
