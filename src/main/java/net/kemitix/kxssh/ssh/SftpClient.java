package net.kemitix.kxssh.ssh;

import net.kemitix.kxssh.scp.ScpUpload;
import net.kemitix.kxssh.scp.ScpDownload;

/**
 * Aggregate interface defining an SFTP client.
 *
 * @author pcampbell
 */
public interface SftpClient extends ScpDownload, ScpUpload {

}
