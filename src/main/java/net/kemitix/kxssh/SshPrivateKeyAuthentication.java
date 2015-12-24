package net.kemitix.kxssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;

/**
 * Represents authentication using a Private Key.
 *
 * @author pcampbell
 */
@Getter
public class SshPrivateKeyAuthentication extends SshAuthentication {

    private final String privateKey;
    private final String passPhrase;

    /**
     * Constructor.
     *
     * @param username   the username to authenticate as
     * @param privateKey the private key to authenticate with
     * @param passPhrase the pass-phrase to open the private key
     */
    public SshPrivateKeyAuthentication(String username, String privateKey, String passPhrase) {
        super(username);
        this.privateKey = privateKey;
        this.passPhrase = passPhrase;
    }

    @Override
    public void prepare(JSch jsch) throws SshException {
        try {
            jsch.addIdentity(privateKey, passPhrase);
        } catch (JSchException ex) {
            throw new SshException("Error adding identity", ex);
        }
    }

    @Override
    public void authenticateSession(Session session) {
        //nothing needed done
    }

}
