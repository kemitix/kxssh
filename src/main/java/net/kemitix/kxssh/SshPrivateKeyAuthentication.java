package net.kemitix.kxssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
     * @param username             the username to authenticate as
     * @param sshPrivateKey        the private key to authenticate with
     * @param privateKeyPassPhrase the pass-phrase to open the private key
     */
    public SshPrivateKeyAuthentication(
            final String username,
            final String sshPrivateKey,
            final String privateKeyPassPhrase) {
        super(username);
        if (!Files.exists(Paths.get(sshPrivateKey))) {
            throw new SshException("private key missing",
                    new FileNotFoundException(sshPrivateKey));
        }
        privateKey = sshPrivateKey;
        passPhrase = privateKeyPassPhrase;
    }

    @Override
    public void prepare(final JSch jsch) {
        try {
            jsch.addIdentity(privateKey, passPhrase);
        } catch (JSchException ex) {
            throw new SshException("Error adding identity", ex);
        }
    }

    @Override
    public void authenticateSession(final Session session) {
        //nothing needed done
    }

}
