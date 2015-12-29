package net.kemitix.kxssh;

import lombok.Getter;

/**
 * Properties for an SSH connection, including the host to connect to and how to
 * authenticate with that host.
 *
 * @author pcampbell
 */
@Getter
@SuppressWarnings("unused")
public class SshConnectionProperties {

    private final String hostname;
    private final SshAuthentication authentication;

    /**
     * Constructor.
     *
     * @param remoteHostname    the host to connect to
     * @param sshAuthentication the authentication details
     */
    public SshConnectionProperties(
            final String remoteHostname,
            final SshAuthentication sshAuthentication) {
        hostname = remoteHostname;
        authentication = sshAuthentication;
    }

}
