package net.kemitix.kxssh;

import java.io.File;

/**
 * Interface defining a command to upload a file via SCP.
 *
 * @author pcampbell
 */
public interface ScpUpload {

    /**
     * Uploads the local file to the remote server.
     *
     * @param localFile      the file to be uploaded
     * @param remoteFilename the file to save as on the remote server
     */
    void upload(File localFile, String remoteFilename);

}
