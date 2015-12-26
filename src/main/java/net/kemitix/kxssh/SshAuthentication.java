package net.kemitix.kxssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Getter;

/**
 * An abstract bundle representing a method of authenticating with a remote
 * server.
 *
 * @author pcampbell
 */
@Getter
public abstract class SshAuthentication {

    /**
     * The user to authenticate as.
     */
    private final String username;

    /**
     * Constructor.
     *
     * @param username the user to authenticate as
     */
    @SuppressWarnings("hiddenfield")
    public SshAuthentication(final String username) {
        this.username = username;
    }

    /**
     * Add the authentication to the session.
     *
     * @param session the session to be authenticated
     */
    public abstract void authenticateSession(Session session);

    /**
     * Prepare the JSCH with the appropriate authentication if needed.
     *
     * @param jsch the JSCH to be prepared
     */
    public abstract void prepare(JSch jsch);

}
