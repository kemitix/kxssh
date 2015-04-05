package net.kemitix.kxssh;

import net.kemitix.kxssh.jsch.JSchSftpClient;

public class KxSsh {

    public static SftpClient getSftpClient(String hostname, String username, String password) {
        SshPasswordAuthentication authentication
                = new SshPasswordAuthentication(username, password);
        SftpClient client
                = getAuthenticatedClient(hostname, authentication);
        return client;
    }

    public static SftpClient getSftpClient(String hostname, String username, String privateKey, String passPhrase) {
        SshPrivateKeyAuthentication authentication
                = new SshPrivateKeyAuthentication(username, privateKey, passPhrase);
        SftpClient client
                = getAuthenticatedClient(hostname, authentication);
        return client;
    }

    private static SftpClient getAuthenticatedClient(String hostname, SshAuthentication authentication) {
        SshConnectionProperties connectionProperties
                = new SshConnectionProperties(hostname, authentication);
        SftpClient client = new JSchSftpClient(connectionProperties);
        return client;
    }

}
