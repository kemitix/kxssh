package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshIOFactory;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.SshStatusListener;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
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
        upload = new JSchScpUpload(connectionProperties);

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
        System.out.println("updateProgress");
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
        System.out.println("updateStatus");
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
        System.out.println("updateProgress no listener");
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
        System.out.println("updateStatus no listener");
        //given
        SshStatus status = SshOperationStatus.CONNECTED;
        upload.setStatusListener(null);

        //when
        upload.updateStatus(status);

        //then
        verify(listener, times(0)).onUpdateStatus(status);
    }

    @Test
    public void testUpload() throws SshException {
        System.out.println("upload(File, String)");
        //given
        upload.setStatusListener(listener);
        upload.setIoChannel(ioChannel);
        when(ioChannel.checkStatus())
                .thenReturn(JSchIOChannel.CONTINUE)
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
    public void testGetInputStreamFileNotFoundException() throws FileNotFoundException, SshException {
        System.out.println("getInputStream FileNotFoundException");
        //given
        upload.setStatusListener(listener);
        upload.setIoChannel(ioChannel);
        upload.setIoFactory(ioFactory);
        when(ioFactory.createFileInputStream(any())).thenThrow(FileNotFoundException.class);

        //when
        upload.upload(localFile, remoteFile);

        //then
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.FILE_OPEN_ERROR);
    }

    @Test(expected = SshException.class)
    public void testUploadIOException() throws SshException, FileNotFoundException, IOException {
        System.out.println("upload(File, String)");
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
