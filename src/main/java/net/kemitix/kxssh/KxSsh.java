package net.kemitix.kxssh;

import net.kemitix.kxssh.jsch.JSchSftpClient;

public class KxSsh {

    public static SftpClient getSftpClient(String hostname, String username, String password) {
        SshPasswordAuthentication authentication
                = new SshPasswordAuthentication(username, password);
        SshConnectionProperties connectionProperties
                = new SshConnectionProperties(hostname, authentication);
        SftpClient client = new JSchSftpClient(connectionProperties);
        return client;
    }

}
