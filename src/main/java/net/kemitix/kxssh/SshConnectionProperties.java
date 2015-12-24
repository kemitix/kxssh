package net.kemitix.kxssh;

import lombok.Getter;

/**
 * Properties for an SSH connection, including the host to connect to and how to
 * authenticate with that host.
 *
 * @author pcampbell
 */
@Getter
public class SshConnectionProperties {

    private final String hostname;
    private final SshAuthentication authentication;

    /**
     * Constructor.
     *
     * @param hostname       the host to connect to
     * @param authentication the authentication details
     */
    public SshConnectionProperties(String hostname, SshAuthentication authentication) {
        this.hostname = hostname;
        this.authentication = authentication;
    }

}
