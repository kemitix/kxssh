package net.kemitix.kxssh;

import java.io.File;

/**
 * Interface defining a command to upload a file via SCP.
 *
 * @author pcampbell
 */
public interface ScpUpload extends SshStatusProvider {

    void upload(File localFile, String remoteFilename) throws SshException;

}
