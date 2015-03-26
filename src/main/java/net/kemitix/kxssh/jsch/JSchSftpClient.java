package net.kemitix.kxssh.jsch;

import java.io.File;
import net.kemitix.kxssh.SftpClient;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshDownload;
import net.kemitix.kxssh.SshException;

public class JSchSftpClient implements SftpClient {

    private final SshConnectionProperties connectionProperties;

    public JSchSftpClient(SshConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    @Override
    public void download(String remoteFilename, File localFile) throws SshException {
        SshDownload download = new JSchDownload(connectionProperties);
        download.download(remoteFilename, localFile);
    }

}
