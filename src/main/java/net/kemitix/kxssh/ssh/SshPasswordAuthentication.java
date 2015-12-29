package net.kemitix.kxssh.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Getter;

/**
 * Represents authentication using a password.
 *
 * @author pcampbell
 */
@Getter
public class SshPasswordAuthentication extends SshAuthentication {

    private final String password;

    /**
     * Constructor.
     *
     * @param username the username to authenticate as
     * @param password the password the authenticate with
     */
    @SuppressWarnings("hiddenfield")
    public SshPasswordAuthentication(
            final String username,
            final String password) {
        super(username);
        this.password = password;
    }

    @Override
    public void prepare(final JSch jsch) {
        // nothing needed done
    }

    @Override
    public void authenticateSession(final Session session) {
        session.setPassword(password);
    }

}
