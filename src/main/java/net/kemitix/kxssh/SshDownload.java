package net.kemitix.kxssh;

public interface SshDownload {

    void download(String remoteFilename, String localFilename) throws SshException;

}
