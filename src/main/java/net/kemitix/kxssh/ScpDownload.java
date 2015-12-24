package net.kemitix.kxssh;

import java.io.File;

/**
 * Interface defining a command to download a file via SCP.
 *
 * @author pcampbell
 */
public interface ScpDownload extends SshStatusProvider {

    /**
     * Downloads the remote file and saves it as the local file.
     *
     * @param remoteFilename the file on the remote server to be downloaded
     * @param localFile      the file to save the remote file as
     *
     * @throws SshException if there is an error in the transfer
     */
    void download(String remoteFilename, File localFile) throws SshException;

}
