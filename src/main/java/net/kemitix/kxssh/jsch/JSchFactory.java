package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class JSchFactory {

    public JSch build() {
        return new JSch();
    }

    public JSch build(String knownHosts) throws SshException, JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(knownHosts);
        if (authentication == null) {
            throw new SshException("Error: authentication not set");
        }
        authentication.prepare(jsch);
        return jsch;
    }

    private SshAuthentication authentication;

    public JSchFactory authenticate(SshAuthentication authentication) {
        this.authentication = authentication;
        return this;
    }

}
