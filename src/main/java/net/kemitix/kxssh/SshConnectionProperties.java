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

    public SshConnectionProperties(String hostname, SshAuthentication authentication) {
        this.hostname = hostname;
        this.authentication = authentication;
    }

}
