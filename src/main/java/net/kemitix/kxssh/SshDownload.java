package net.kemitix.kxssh;

import java.io.File;

public interface SshDownload {

    void download(String remoteFilename, File localFile) throws SshException;

}
