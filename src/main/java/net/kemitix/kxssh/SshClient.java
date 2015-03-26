package net.kemitix.kxssh;

public class SshClient {

    public static SftpClient getSftpClient(String hostname, String username, String password) {
        SshPasswordAuthentication authentication
                = new SshPasswordAuthentication(username, password);
        SshConnectionProperties connectionProperties
                = new SshConnectionProperties(hostname, authentication);
        SftpClient client = new JSchSftpClient(connectionProperties);
        return client;
    }

}