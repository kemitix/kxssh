package net.kemitix.kxssh;

import net.kemitix.kxssh.jsch.JSchSftpClient;

/**
 * Helper class for constructing {@link SftpClient} instances.
 *
 * @author pcampbell
 */
public interface KxSsh {

    /**
     * Creates an {@link SftpClient} for a host using username/password
     * authentication.
     *
     * @param hostname the host to connect to
     * @param username the user to authenticate as
     * @param password the password to authenticate with
     *
     * @return an {@link SftpClient}
     */
    static SftpClient getSftpClient(
            final String hostname,
            final String username,
            final String password) {
        SshPasswordAuthentication authentication
                = new SshPasswordAuthentication(username, password);
        SftpClient client
                = getAuthenticatedClient(hostname, authentication);
        return client;
    }

    /**
     * Creates an {@link SftpClient} for a host using public/private key
     * authentication.
     *
     * @param hostname   the host to connect to
     * @param username   the user to authenticate as
     * @param privateKey the private key to authenticate with
     * @param passPhrase the password for the private key
     *
     * @return an {@link SftpClient}
     */
    static SftpClient getSftpClient(
            final String hostname,
            final String username,
            final String privateKey,
            final String passPhrase) {
        SshPrivateKeyAuthentication authentication
                = new SshPrivateKeyAuthentication(username, privateKey,
                        passPhrase);
        SftpClient client
                = getAuthenticatedClient(hostname, authentication);
        return client;
    }

    /**
     * Creates an {@link SftpClient} for a host using a pre-build
     * {@link SshAuthentication}.
     *
     * @param hostname       the host to connect to
     * @param authentication the existing authentication bundle
     *
     * @return an {@link SftpClient}
     */
    static SftpClient getAuthenticatedClient(
            final String hostname,
            final SshAuthentication authentication) {
        SshConnectionProperties connectionProperties
                = new SshConnectionProperties(hostname, authentication);
        SftpClient client = new JSchSftpClient(connectionProperties);
        return client;
    }

}
