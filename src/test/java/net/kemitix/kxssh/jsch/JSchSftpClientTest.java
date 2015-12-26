package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.SshStatusListener;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchSftpClientTest {

    private JSchSftpClient client;
    private Session session;
    private JSchFactory jSchFactory;
    private JSch jsch;
    private SshConnectionProperties connectionProperties;
    private SshAuthentication authentication;
    private final String username = "username";
    private final String hostname = "hostname";
    private SshStatusListener statusListener;
    private JSchScpDownload download;
    private JSchScpUpload upload;
    private String remote;
    private File local;

    @Before
    public void setUp() throws IOException, JSchException {
        connectionProperties = mock(SshConnectionProperties.class);
        authentication = mock(SshPasswordAuthentication.class);
        statusListener = mock(SshStatusListener.class);
        download = mock(JSchScpDownload.class);
        upload = mock(JSchScpUpload.class);
        session = mock(Session.class);
        jSchFactory = mock(JSchFactory.class);
        jsch = mock(JSch.class);

        when(connectionProperties.getAuthentication()).thenReturn(authentication);
        when(connectionProperties.getHostname()).thenReturn(hostname);
        when(authentication.getUsername()).thenReturn(username);

        when(download.getJSch(authentication)).thenReturn(jsch);
        when(upload.getJSch(authentication)).thenReturn(jsch);

        when(jSchFactory.authenticate(any())).thenReturn(jSchFactory);
        when(jSchFactory.knownHosts(any())).thenReturn(jSchFactory);
        when(jSchFactory.build()).thenReturn(jsch);
        when(jsch.getSession(username, hostname)).thenReturn(session);

        client = new JSchSftpClient(connectionProperties);
        client.setDownload(download);
        client.setUpload(upload);
        client.setStatusListener(statusListener);

        remote = "remote.txt";
        local = File.createTempFile("local", ".txt");
    }

    @After
    public void tearDown() {
        local.delete();
    }

    /**
     * Test of download method, of class JSchSftpClient.
     */
    @Test
    public void testDownload() {
        //given

        //when
        client.download(remote, local);

        //then
        verify(download, times(1)).download(remote, local);
    }

    /**
     * Test of download method, of class JSchSftpclient.
     *
     * When download not already set
     */
    @Test
    public void testDownloadDownloadNotSet() {
        //given
        client.setDownload(null);

        //when
        client.requireDownload();

        //then
    }

    /**
     * Test of updateProgress method, of class JSchSftpClient.
     */
    @Test
    public void testUpdateProgress() {
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
        //given
        SshStatus status = SshOperationStatus.CONNECTED;

        //when
        client.updateStatus(status);

        //then
        verify(statusListener, times(1)).onUpdateStatus(status);
    }

    /**
     * Test of upload method, of class JSchSftpClient.
     */
    @Test
    public void testUpload() {
        //given

        //when
        client.upload(local, remote);

        //then
        verify(upload, times(1)).upload(local, remote);
    }

    /**
     * Test of upload method, of class JSchSftpclient.
     *
     * When upload not already set
     */
    @Test
    public void testUploadUploadNotSet() {
        //given
        client.setUpload(null);

        //when
        client.requireUpload();

        //then
    }
}
