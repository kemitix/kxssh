package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.ssh.SshAuthentication;
import net.kemitix.kxssh.ssh.SshException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

/**
 * Factory class to create an authenticated {@link JSch} connection.
 *
 * @author pcampbell
 */
public class JSchFactory {

    /**
     * Creates the {@link JSch} object.
     *
     * @return the created {@link JSch}
     *
     * @throws JSchException if error setting the known_hosts filename
     */
    public JSch build() throws JSchException {
        JSch jsch = new JSch();
        if (knownHosts != null) {
            jsch.setKnownHosts(knownHosts);
        }
        if (authentication == null) {
            throw new SshException("Error: authentication not set");
        }
        authentication.prepare(jsch);
        return jsch;
    }

    private SshAuthentication authentication;

    /**
     * Sets the authentication method.
     *
     * @param sshAuthentication the authentication method
     *
     * @return the factory to allow chained methods
     */
    public JSchFactory authenticate(final SshAuthentication sshAuthentication) {
        authentication = sshAuthentication;
        return this;
    }

    private String knownHosts;

    /**
     * Sets the name of the known_hosts file.
     *
     * @param knownHostsFilename the filename of the known_hosts file
     *
     * @return the factory to allow chained methods
     */
    public JSchFactory knownHosts(final String knownHostsFilename) {
        knownHosts = knownHostsFilename;
        return this;
    }
}
