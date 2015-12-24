package net.kemitix.kxssh;

import java.io.File;

/**
 * Interface defining a command to download a file via SCP.
 *
 * @author pcampbell
 */
public interface ScpDownload extends SshStatusProvider {

    void download(String remoteFilename, File localFile) throws SshException;

}
