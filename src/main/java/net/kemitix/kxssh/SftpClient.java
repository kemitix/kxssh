package net.kemitix.kxssh;

public interface SftpClient {

    void download(String remoteFilename, String localFilename) throws SshException;

}
