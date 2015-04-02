package net.kemitix.kxssh.jsch;

import java.io.File;
import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.SshStatusListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchSftpClientTest {

    private JSchSftpClient client;
    private SshConnectionProperties connectionProperties;
    private SshAuthentication authentication;
    private SshStatusListener statusListener;
    private JSchScpDownload download;

    @Before
    public void setUp() {
        connectionProperties = mock(SshConnectionProperties.class);
        authentication = mock(SshPasswordAuthentication.class);
        statusListener = mock(SshStatusListener.class);
        download = mock(JSchScpDownload.class);
        client = new JSchSftpClient(connectionProperties);
        client.setDownload(download);
        client.setStatusListener(statusListener);
    }

    /**
     * Test of download method, of class JSchSftpClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDownload() throws Exception {
        System.out.println("download");
        //given
        String remote = "remote.txt";
        File local = new File("local.txt");

        //when
        client.download(remote, local);

        //then
        verify(download, times(1)).download(remote, local);
    }

    /**
     * Test of download method, of class JSchSftpclient.
     *
     * When download not already set
     *
     * @throws net.kemitix.kxssh.SshException
     */
    @Test
    public void testDownloadDownloadNotSet() throws SshException {
        System.out.println("download download not set");
        //given
        client.setDownload(null);

        when(connectionProperties.getAuthentication()).thenReturn(authentication);
        when(connectionProperties.getHostname()).thenReturn("hostname");
        when(authentication.getUsername()).thenReturn("username");

        //when
        client.requireDownload();

        //then
    }

    /**
     * Test of updateProgress method, of class JSchSftpClient.
     */
    @Test
    public void testUpdateProgress() {
        System.out.println("updateProgress");
        //given
        int progress = 50;
        int total = 100;

        //when
        client.updateProgress(progress, total);

        //then
        verify(statusListener, times(1)).onUpdateProgress(progress, total);
    }

    /**
     * Test of updateStatus method, of class JSchSftpClient.
     */
    @Test
    public void testUpdateStatus() {
        System.out.println("updateStatus");
        //given
        SshStatus status = SshOperationStatus.CONNECTED;

        //when
        client.updateStatus(status);

        //then
        verify(statusListener, times(1)).onUpdateStatus(status);
    }

}
