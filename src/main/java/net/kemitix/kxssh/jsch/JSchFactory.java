package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

public class JSchFactory {

    public JSch build() {
        return new JSch();
    }

    public JSch build(String knownHosts) throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(knownHosts);
        return jsch;
    }

}
