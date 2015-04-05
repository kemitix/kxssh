package net.kemitix.kxssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Getter;

@Getter
public class SshPasswordAuthentication extends SshAuthentication {

    private final String password;

    SshPasswordAuthentication(String username, String password) {
        super(username);
        this.password = password;
    }

    @Override
    public void prepare(JSch jsch) {
        // nothing needed done
    }

    @Override
    public void authenticateSession(Session session) {
        session.setPassword(password);
    }

}
