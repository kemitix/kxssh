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

    private final String username;

    public SshAuthentication(String username) {
        this.username = username;
    }

    public abstract void authenticateSession(Session session);

    public abstract void prepare(JSch jsch) throws SshException;

}
