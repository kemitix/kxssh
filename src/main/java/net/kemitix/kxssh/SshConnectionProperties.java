package net.kemitix.kxssh;

import lombok.Getter;

@Getter
public class SshConnectionProperties {

    private final String hostname;
    private final SshAuthentication authentication;

    public SshConnectionProperties(String hostname, SshAuthentication authentication) {
        this.hostname = hostname;
        this.authentication = authentication;
    }

}
