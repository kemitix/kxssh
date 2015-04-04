package net.kemitix.kxssh;

import java.io.File;

public interface ScpDownload extends SshStatusProvider {

    void download(String remoteFilename, File localFile) throws SshException;

}
