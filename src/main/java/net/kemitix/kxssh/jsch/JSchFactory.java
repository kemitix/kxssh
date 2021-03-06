package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshException;

public class JSchFactory {

    public JSch build() throws SshException, JSchException {
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

    public JSchFactory authenticate(SshAuthentication authentication) {
        this.authentication = authentication;
        return this;
    }

    private String knownHosts;

    public JSchFactory knownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
        return this;
    }
}
