package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.ssh.SshConnectionProperties;
import net.kemitix.kxssh.ssh.SshErrorStatus;
import net.kemitix.kxssh.ssh.SshException;
import net.kemitix.kxssh.ssh.SshIOFactory;
import net.kemitix.kxssh.ssh.SshOperationStatus;
import net.kemitix.kxssh.ssh.SshPasswordAuthentication;
import net.kemitix.kxssh.ssh.SshStatus;
import net.kemitix.kxssh.ssh.SshStatusListener;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchScpUploadTest {

    private JSchScpUpload upload;
    private SshConnectionProperties connectionProperties;
    private SshPasswordAuthentication authentication;
    private Session session;
    private JSchFactory jschFactory;
    private JSch jsch;
    private File localFile;
    private String remoteFile;
    private SshStatusListener listener;
    private JSchIOChannel ioChannel;
    private SshIOFactory ioFactory;
    private FileInputStream input;
    private FileOutputStream output;
    private long filesize;

    @Before
    public void setUp() throws IOException {
        remoteFile = "remote.txt";
        localFile = File.createTempFile("local", ".txt");
        connectionProperties = mock(SshConnectionProperties.class);
        authentication = mock(SshPasswordAuthentication.class);
        session = mock(Session.class);
        ioFactory = mock(SshIOFactory.class);
        jschFactory = mock(JSchFactory.class);
        jsch = mock(JSch.class);
        listener = mock(SshStatusListener.class);
        ioChannel = mock(JSchIOChannel.class);
        input = mock(FileInputStream.class);
        output = mock(FileOutputStream.class);
        connectionProperties = mock(SshConnectionProperties.class);
        upload = new JSchScpUpload(connectionProperties, jschFactory,
                ioFactory);

        byte[] buffer = "test file".getBytes("UTF-8");
        new FileOutputStream(localFile).write(buffer);
        filesize = buffer.length;
    }

    @After
    public void tearDown() {
        localFile.delete();
    }

    @Test
    public void testLocalFileSize() {
        assertTrue(localFile.length() > 0);
        assertThat(localFile.length(), is(filesize));
    }

    @Test
    public void testUpdateProgress() {
        //given
        long position = 50;
        long total = 100;
        upload.setStatusListener(listener);

        //when
        upload.updateProgress(position, total);

        //then
        verify(listener, times(1)).onUpdateProgress(position, total);
    }

    @Test
    public void testUpdateStatus() {
        //given
        SshStatus status = SshOperationStatus.CONNECTED;
        upload.setStatusListener(listener);

        //when
        upload.updateStatus(status);

        //then
        verify(listener, times(1)).onUpdateStatus(status);
    }

    @Test
    public void testUpdateProgressNoListener() {
        //given
        long position = 50;
        long total = 100;
        upload.setStatusListener(null);

        //when
        upload.updateProgress(position, total);

        //then
        verify(listener, times(0)).onUpdateProgress(position, total);
    }

    @Test
    public void testUpdateStatusNoListener() {
        //given
        SshStatus status = SshOperationStatus.CONNECTED;
        upload.setStatusListener(null);

        //when
        upload.updateStatus(status);

        //then
        verify(listener, times(0)).onUpdateStatus(status);
    }

    @Test
    public void testUpload() {
        //given
        upload.setStatusListener(listener);
        upload.setIoChannel(ioChannel);
        when(ioChannel.checkStatus())
                .thenReturn(JSchIOChannel.SUCCESS);

        //when
        upload.upload(localFile, remoteFile);

        //then
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.STARTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.UPLOADING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.DISCONNECTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.DISCONNECTED);
    }

    @Test(expected = SshException.class)
    public void testGetInputStreamFileNotFoundException() throws FileNotFoundException {
        //given
        upload.setStatusListener(listener);
        upload.setIoChannel(ioChannel);
        when(ioFactory.createFileInputStream(any())).thenThrow(FileNotFoundException.class);

        //when
        upload.upload(localFile, remoteFile);

        //then
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.FILE_OPEN_ERROR);
    }

    @Test(expected = SshException.class)
    public void testUploadIOException() throws FileNotFoundException, IOException {
        //given
        upload.setStatusListener(listener);
        upload.setIoChannel(ioChannel);
        doThrow(IOException.class).when(ioChannel).readFromStream(any(), eq(filesize));

        //when
        upload.upload(localFile, remoteFile);

        //then
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.STARTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.UPLOADING);
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.FILE_OPEN_ERROR);
        verify(listener, times(0)).onUpdateStatus(SshOperationStatus.DISCONNECTING);
        verify(listener, times(0)).onUpdateStatus(SshOperationStatus.DISCONNECTED);
    }
}
