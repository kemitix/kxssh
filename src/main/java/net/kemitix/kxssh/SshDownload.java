package net.kemitix.kxssh;

import java.io.File;

public interface SshDownload extends StatusProvider {

    void download(String remoteFilename, File localFile) throws SshException;

}
