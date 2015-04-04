package net.kemitix.kxssh;

import java.io.File;

public interface ScpUpload extends SshStatusProvider {

    void upload(File localFile, String remoteFilename) throws SshException;

}
