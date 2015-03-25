package net.kemitix.kxssh;

import lombok.Getter;

@Getter
public class SshPasswordAuthentication extends SshAuthentication {

    private final String password;

    SshPasswordAuthentication(String username, String password) {
        super(username);
        this.password = password;
    }

}
