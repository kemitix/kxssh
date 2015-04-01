package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshIOFactory;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.StatusListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchDownloadTest {

    private JSchDownload download;
    private Session session;
    private JSchIOChannel ioChannel;
    private IOChannelMetadata metaData;
    private int filesize;
    private StatusListener listener;
    private SshIOFactory factory;
    private FileOutputStream outputStream;

    @Before
    public void setUp() {
        SshConnectionProperties connectionProperties = mock(SshConnectionProperties.class);
        session = mock(Session.class);
        ioChannel = mock(JSchIOChannel.class);
        metaData = mock(IOChannelMetadata.class);
        listener = mock(StatusListener.class);
        factory = mock(SshIOFactory.class);
        outputStream = mock(FileOutputStream.class);
        download = new JSchDownload(connectionProperties);
        download.setSession(session);
        download.setIoChannel(ioChannel);
        download.setStatusListener(listener);
        download.setIoFactory(factory);
    }

    /**
     * Test of download method, of class JSchDownload.
     *
     * Simulates downloading a 231 byte file, which should be completed using a
     * single loop/read from the InputStream.
     *
     * @throws java.lang.Exception
     */
    @Test(timeout = 100L)
    public void testDownload() throws Exception {
        System.out.println("download");
        //given
        String remote = "remote.txt";
        filesize = 231;
        File localFile = new File("local.txt");
        when(ioChannel.checkStatus())
                .thenReturn(JSchIOChannel.CONTINUE)
                .thenReturn(JSchIOChannel.SUCCESS)
                .thenReturn(JSchIOChannel.EOF);
        when(ioChannel.readMetaData()).thenReturn(metaData);
        IOChannelReadReply reply = mock(IOChannelReadReply.class);
        when(reply.getBytesRead()).thenReturn(filesize);
        byte[] buffer = new byte[filesize];
        when(reply.getBuffer()).thenReturn(buffer);
        when(ioChannel.read(eq(filesize))).thenReturn(reply);
        when(metaData.getFilesize()).thenReturn(filesize);
        when(factory.createFileOutputStream(localFile)).thenReturn(outputStream);

        //when
        download.download(remote, localFile);

        //then
        verify(ioChannel, times(1)).setExecCommand(contains(remote));
        verify(ioChannel, times(1)).setLocalFile(localFile);
        verify(ioChannel, times(2)).notifyReady();// top loop 1, bottom loop 1
        verify(ioChannel, times(3)).checkStatus();// start of loop 1, bottom of loop 1, top of loop 2
        verify(ioChannel, times(1)).readMetaData();
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.STARTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.DOWNLOADING);
        verify(listener, times(1)).onUpdateProgress(0, filesize);
        verify(listener, times(2)).onUpdateProgress(filesize, filesize);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.DISCONNECTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.DISCONNECTED);
    }

    /**
     * Test of download method, of class JSchDownload.
     *
     * With checkStatus not reporting Success at bottom of loop
     *
     * @throws java.lang.Exception
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testDownloadFailure() throws Exception {
        System.out.println("download w/failure");
        //given
        String remote = "remote.txt";
        filesize = 231;
        File localFile = new File("local.txt");
        when(ioChannel.checkStatus())
                .thenReturn(JSchIOChannel.CONTINUE)
                .thenReturn(JSchIOChannel.ERROR);// i.e. not SUCCESS
        when(ioChannel.readMetaData()).thenReturn(metaData);
        IOChannelReadReply reply = mock(IOChannelReadReply.class);
        when(reply.getBytesRead()).thenReturn(filesize);
        byte[] buffer = new byte[filesize];
        when(reply.getBuffer()).thenReturn(buffer);
        when(ioChannel.read(eq(filesize))).thenReturn(reply);
        when(metaData.getFilesize()).thenReturn(filesize);
        when(factory.createFileOutputStream(localFile)).thenReturn(outputStream);

        //when
        download.download(remote, localFile);

        //then
        verify(ioChannel, times(1)).setExecCommand(contains(remote));
        verify(ioChannel, times(1)).setLocalFile(localFile);
        verify(ioChannel, times(1)).connect();
        verify(ioChannel, times(3)).notifyReady();// pre-loop, top loop 1, bottom loop 1
        verify(ioChannel, times(2)).checkStatus();// start of loop 1, bottom of loop 1
        verify(ioChannel, times(1)).readMetaData();
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.STARTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.CONNECTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.CONNECTED);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.DOWNLOADING);
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.ACK_ERROR);
    }

    /**
     * Test of download method, of class JSchDownload.
     *
     * With FileNotFoundException
     *
     * @throws java.lang.Exception
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testDownloadLocalFileNotFound() throws Exception {
        System.out.println("download w/local file not found");
        //given
        String remote = "remote.txt";
        File localFile = new File("local.txt");
        when(ioChannel.checkStatus())
                .thenReturn(JSchIOChannel.CONTINUE);
        when(ioChannel.readMetaData()).thenReturn(metaData);
        when(factory.createFileOutputStream(localFile))
                .thenThrow(FileNotFoundException.class);

        //when
        download.download(remote, localFile);

        //then
        verify(ioChannel, times(1)).setExecCommand(contains(remote));
        verify(ioChannel, times(1)).setLocalFile(localFile);
        verify(ioChannel, times(1)).connect();
        verify(ioChannel, times(2)).notifyReady();// pre-loop, top loop 1
        verify(ioChannel, times(1)).checkStatus();// start of loop 1
        verify(ioChannel, times(1)).readMetaData();
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.STARTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.CONNECTING);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.CONNECTED);
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.DOWNLOADING);
    }

}
