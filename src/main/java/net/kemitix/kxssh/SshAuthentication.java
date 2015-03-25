package net.kemitix.kxssh;

import lombok.Getter;

@Getter
public class SshAuthentication {

    private final String username;

    public SshAuthentication(String username) {
        this.username = username;
    }
}
