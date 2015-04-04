package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.kemitix.kxssh.IOChannelReadReply;
import net.kemitix.kxssh.IOChannelReadReplyFactory;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.SshStatusListener;
import net.kemitix.kxssh.scp.ScpCopyCommand;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchIOChannelTest {

    private JSchIOChannel ioChannel;

    private IOChannelReadReplyFactory readReplyFactory;
    private Channel channel;
    private OutputStream output;
    private InputStream input;
    private String remoteFilename;
    private File localFile;
    private SshStatusListener listener;

    @Before
    public void setUp() throws IOException {
        ioChannel = new JSchIOChannel();

        readReplyFactory = mock(IOChannelReadReplyFactory.class);
        channel = mock(ChannelExec.class);
        output = mock(OutputStream.class);
        input = mock(InputStream.class);
        remoteFilename = "remote.txt";
        localFile = mock(File.class);
        listener = mock(SshStatusListener.class);

        when(channel.getInputStream()).thenReturn(input);
        when(channel.getOutputStream()).thenReturn(output);
    }

    /**
     * Tests of createExecIOChannel method, of class JSchIOChannel.
     *
     * @throws com.jcraft.jsch.JSchException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test
    public void testCreateExecIOChannel() throws JSchException, SshException {
        System.out.println("createExecIOChannel");
        //given
        Session session = mock(Session.class);
        when(session.openChannel("exec")).thenReturn(channel);

        //when
        JSchIOChannel execChannel = JSchIOChannel.createExecIOChannel(session);

        //then
        assertThat(execChannel.getChannel(), is(channel));
    }

    /**
     * Tests of createExecIOChannel method, of class JSchIOChannel.
     *
     * Throws JSchException
     *
     * @throws com.jcraft.jsch.JSchException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class)
    public void testCreateExecIOChannelThrowJSchException() throws JSchException, SshException {
        System.out.println("createExecIOChannel throws JSchException");
        //given
        Session session = mock(Session.class);
        when(session.openChannel("exec")).thenThrow(JSchException.class);

        //when
        JSchIOChannel.createExecIOChannel(session);

        //then
    }

    /**
     * Tests of createExecIOChannel method, of class JSchIOChannel.
     *
     * Throws IOException
     *
     * @throws com.jcraft.jsch.JSchException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class)
    public void testCreateExecIOChannelThrowIOException() throws JSchException, SshException {
        System.out.println("createExecIOChannel throws IOException");
        //given
        Session session = mock(Session.class);
        when(session.openChannel("exec")).thenThrow(IOException.class);

        //when
        JSchIOChannel.createExecIOChannel(session);

        //then
    }

    /**
     * Test of read(int) method, of class JSchIOChannel.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(timeout = 100L)
    public void testRead() throws SshException, IOException {
        System.out.println("read(int)");
        //given
        ioChannel.setInput(input);
        byte[] buffer = new byte[10];
        int offset = 0;
        int length = 10;
        when(input.read(buffer, offset, length)).thenReturn(length);
        ioChannel.setReadReplyFactory(readReplyFactory);
        IOChannelReadReply reply = mock(IOChannelReadReply.class);
        when(readReplyFactory.createReply(eq(length), eq(length), any()))
                .thenReturn(reply);
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);

        //when
        IOChannelReadReply result = ioChannel.read(length);

        //then
        assertThat(result, is(reply));
    }

    /**
     * Test for read method, of class JSchIOChannel.
     *
     * Where reading throws an IOException
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testReadIOException() throws IOException, SshException {
        System.out.println("read IOException");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        int length = 1;
        when(input.read(any(), eq(0), eq(length))).thenThrow(IOException.class);

        //when
        ioChannel.read(length);
    }

    /**
     * Test of read(int) method, of class JSchIOChannel.
     *
     * Reach end of stream.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testReadEndOfStream() throws SshException, IOException {
        System.out.println("read(int) reach end of stream");
        //given
        ioChannel.setInput(input);
        byte[] buffer = new byte[10];
        int offset = 0;
        int length = 10;
        when(input.read(buffer, offset, length)).thenReturn(-1);

        //when
        ioChannel.read(length);

        //then
    }

    /**
     * Test of readScpCommand method, of class JSchIOChannel.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(timeout = 100L)
    public void testReadMetaData() throws SshException, IOException {
        System.out.println("readMetaData");
        //given
        ioChannel.setChannel(channel);
        ioChannel.setReadReplyFactory(readReplyFactory);
        when(channel.isConnected()).thenReturn(true);

        IOChannelReadReply headerReply = mock(IOChannelReadReply.class);
        byte[] headerBuffer = new byte[4];
        headerBuffer[0] = '0';
        headerBuffer[1] = '7';
        headerBuffer[2] = '6';
        headerBuffer[3] = '4';
        when(headerReply.getBuffer()).thenReturn(headerBuffer);

        when(input.read())
                .thenReturn((int) 'C', (int) '0', (int) '7', (int) '6', (int) '4', (int) ' ', (int) '7', (int) ' ', (int) 'f', (int) '\r');

        //when
        ScpCopyCommand scpCopyCommand = (ScpCopyCommand) ioChannel.readScpCommand();

        //then
        assertThat(scpCopyCommand.getFileMode(), is(headerBuffer));
        assertThat(scpCopyCommand.getLength(), is(7L));
        assertThat(scpCopyCommand.getName(), is("f"));
    }

    /**
     * Test of checkStatus() method, of class JSchIOChannel.
     *
     * Status SUCCESS
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(timeout = 100L)
    public void testCheckStatusSuccess() throws IOException, SshException {
        System.out.println("checkStatus SUCCESS");
        //given
        ioChannel.setChannel(channel);
        when(input.read()).thenReturn(JSchIOChannel.SUCCESS);
        when(channel.isConnected()).thenReturn(true);

        //when
        int status = ioChannel.checkStatus();

        //then
        assertThat(status, is(JSchIOChannel.SUCCESS));
    }

    /**
     * Test of checkStatus() method, of class JSchIOChannel.
     *
     * Status EOF
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(timeout = 100L)
    public void testCheckStatusEOF() throws IOException, SshException {
        System.out.println("checkStatus EOF");
        //given
        ioChannel.setChannel(channel);
        when(input.read()).thenReturn(JSchIOChannel.EOF);
        when(channel.isConnected()).thenReturn(true);

        //when
        int status = ioChannel.checkStatus();

        //then
        assertThat(status, is(JSchIOChannel.EOF));
    }

    /**
     * Test of checkStatus() method, of class JSchIOChannel.
     *
     * Status CONTINUE
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(timeout = 100L)
    public void testCheckStatusContinue() throws IOException, SshException {
        System.out.println("checkStatus CONTINUE");
        //given
        ioChannel.setChannel(channel);
        when(input.read()).thenReturn(JSchIOChannel.CONTINUE);
        when(channel.isConnected()).thenReturn(true);

        //when
        int status = ioChannel.checkStatus();

        //then
        assertThat(status, is(JSchIOChannel.CONTINUE));
    }

    /**
     * Test of checkStatus() method, of class JSchIOChannel.
     *
     * Status ERROR
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testCheckStatusError() throws IOException, SshException {
        System.out.println("checkStatus ERROR");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        when(input.read())
                .thenReturn(JSchIOChannel.ERROR)
                .thenReturn((int) '\n');

        //when
        ioChannel.checkStatus();

        //then
    }

    /**
     * Test of checkStatus() method, of class JSchIOChannel.
     *
     * Status FATAL
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testCheckStatusFatal() throws IOException, SshException {
        System.out.println("checkStatus FATAL");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        when(input.read())
                .thenReturn(JSchIOChannel.FATAL)
                .thenReturn((int) 'a')
                .thenReturn((int) '\n');

        //when
        ioChannel.checkStatus();

        //then
    }

    /**
     * Test of checkStatus() method, of class JSchIOChannel.
     *
     * Throws an IOException
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testCheckStatusIOEXception() throws IOException, SshException {
        System.out.println("checkStatus IOException");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        when(input.read()).thenThrow(IOException.class);

        //when
        ioChannel.checkStatus();

        //then
    }

    /**
     * Test for notifyReady() method, of class JSchIOChannel.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(timeout = 100L)
    public void testNotifyReady() throws SshException, IOException {
        System.out.println("notifyReady");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);

        //when
        ioChannel.notifyReady();

        //then
        verify(output, times(1)).write(any(), eq(0), eq(1));
        verify(output, times(1)).flush();
    }

    /**
     * Test for notifyReady() method, of class JSchIOChannel.
     *
     * Throws an IOException
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testNotifyReadyIOException() throws SshException, IOException {
        System.out.println("notifyReady throws IOException");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        doThrow(IOException.class)
                .when(output)
                .flush();

        //when
        ioChannel.notifyReady();

        //then
        verify(output, times(1)).write(any(), eq(0), eq(1));
        verify(output, times(1)).flush();
    }

    /**
     * Test for setRemoteDownloadFilename method, of class JSchIOChannel.
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test
    public void testSetRemoteFilename() throws IOException, SshException {
        System.out.println("setRemoteFilename");
        //given
        ioChannel.setChannel(channel);

        //when
        ioChannel.setExecCommand(remoteFilename);

        //then
        verify((ChannelExec) channel, times(1)).setCommand(contains(remoteFilename));
    }

    /**
     * Test for connect method, of class JSchIOChannel.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testConnect() throws SshException, IOException, JSchException {
        System.out.println("connect");
        //given
        ioChannel.setChannel(channel);

        //when
        ioChannel.connect();

        //then
        verify(channel, times(1)).connect();
    }

    /**
     * Test for connect method, of class JSchIOChannel.
     *
     * Throws a JSchException
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test(expected = SshException.class)
    public void testConnectJSchException() throws SshException, IOException, JSchException {
        System.out.println("connect throws JSchException");
        //given
        ioChannel.setChannel(channel);
        doThrow(JSchException.class)
                .when(channel)
                .connect();

        //when
        ioChannel.connect();

        //then
        verify(channel, times(1)).connect();
    }

    /**
     * Test for set/getLocalFile() methods, of class JSchIOChannel.
     */
    @Test
    public void testSetGetLocalFile() {
        System.out.println("set/getLocalFile");
        //given

        //when
        ioChannel.setLocalFile(localFile);

        //then
        assertThat(ioChannel.getLocalFile(), is(localFile));
    }

    /**
     * Test for set/getOutput methods, of class JSchIOChannel.
     */
    @Test
    public void testSetGetOutput() {
        System.out.println("set/getOutput");
        //given
        ioChannel.setOutput(output);

        //when
        OutputStream result = ioChannel.getOutput();

        //then
        assertThat(result, is(output));
    }

    /**
     * Test for setChannel method, of class JSchIOChannel.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testSetChannel() throws IOException {
        System.out.println("setChannel");
        //given

        //when
        ioChannel.setChannel(channel);

        //then
        assertThat(ioChannel.getInput(), is(input));
        assertThat(ioChannel.getOutput(), is(output));
    }

    /**
     * Test for set/getInput methods, of class JSchIOChannel.
     */
    @Test
    public void testSetGetInput() {
        System.out.println("set/getInput");
        //given
        ioChannel.setInput(input);

        //when
        InputStream result = ioChannel.getInput();

        //then
        assertThat(result, is(input));
    }

    /**
     * Test for set/getRemoteFilename methods, of class JSchIOChannel.
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test
    public void testSetGetRemoteFilename() throws IOException, SshException {
        System.out.println("set/getRemoteFilename");
        //given
        ioChannel.setChannel(channel);

        //when
        ioChannel.setExecCommand(remoteFilename);

        //then
        verify((ChannelExec) channel).setCommand(contains(remoteFilename));
    }

    /**
     * Test for set/getReadReplyFactory methods, of class JSchIOChannel.
     */
    @Test
    public void testSetGetReadReplyFactory() {
        System.out.println("set/getReadReplyFactory");
        //given

        //when
        ioChannel.setReadReplyFactory(readReplyFactory);

        //then
        assertThat(ioChannel.getReadReplyFactory(), is(readReplyFactory));
    }

    /**
     * Test for connect method, of class JSchIOChannel.
     *
     * When not connected
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test
    public void testConnectNotConnected() throws SshException, IOException, JSchException {
        System.out.println("connect not connected");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(false);

        //when
        ioChannel.connect();

        //then
        verify(channel, times(1)).connect();
    }

    /**
     * Test for connect method, of class JSchIOChannel.
     *
     * When connected
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test
    public void testConnectConnected() throws SshException, IOException {
        System.out.println("connect connected");
        //given
        ioChannel.setChannel(channel);

        //when
        ioChannel.connect();

        //then
    }

    /**
     * Test for disconnect method, of class JSchIOChannel.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testDisconnect() throws IOException {
        System.out.println("disconnect");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);

        //when
        ioChannel.disconnect();

        //then
        verify(channel, times(1)).disconnect();
    }

    /**
     * Test for disconnect method, of class JSchIOChannel.
     *
     * When not connected
     *
     * @throws java.io.IOException
     */
    @Test
    public void testDisconnectNotConnected() throws IOException {
        System.out.println("disconnect");
        //given

        //when
        ioChannel.disconnect();

        //then
        verify(channel, times(0)).disconnect();
        assertFalse(ioChannel.isConnected());
    }

    /**
     * Test for requireConnection method, of class JSchIOChannel.
     *
     * When connected
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test
    public void testRequireConnectionConnected() throws SshException, IOException {
        System.out.println("requireConnection connected");
        //given
        ioChannel.setChannel(channel);

        //when
        ioChannel.setExecCommand(remoteFilename);

        //then
    }

    /**
     * Test for requireConnection method, of class JSchIOChannel.
     *
     * When not connected
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testRequireNotConnectionConnected() throws SshException, IOException {
        System.out.println("requireConnection not connected");
        //given

        //when
        ioChannel.readToEol('\n');

        //then
    }

    /**
     * Test for isConnected method, of class JSchIOChannel.
     *
     * Where channel is not set
     *
     * @throws java.io.IOException
     */
    @Test
    public void testIsConnectedNoChannel() throws IOException {
        System.out.println("isConnected w/no channel");
        //given
        ioChannel.setChannel(null);

        //when
        boolean result = ioChannel.isConnected();

        //then
        assertFalse(result);
    }

    /**
     * Test for connect method, of class JSchIOChannel.
     *
     * Where already connected
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testConnectIsConnected() throws IOException, SshException, JSchException {
        System.out.println("connect is connected");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);

        //when
        ioChannel.connect();

        //then
        verify(channel, times(0)).connect();
    }

    /**
     * Test for read method, of class JSchIOChannel.
     *
     * Where reading past the end of the stream
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testReadEof() throws IOException, SshException {
        System.out.println("read eof");
        //given
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        int length = 1;
        when(input.read(any(), eq(0), eq(length))).thenReturn(JSchIOChannel.EOF);

        //when
        ioChannel.read(length);
    }

    /**
     * Test of writeToStream method, of class JSchScpOperation
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(timeout = 100L)
    public void testWriteToStream() throws IOException, SshException {
        System.out.println("writeToStream");
        //given
        int filesize = 100;
        int chunk = 50;
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        when(input.read(any(), eq(0), eq(filesize))).thenReturn(chunk);
        when(input.read(any(), eq(0), eq(chunk))).thenReturn(chunk);
        ioChannel.setStatusListener(listener);

        //when
        ioChannel.writeToStream(output, filesize);

        //then
        verify(output, times(2)).write(any(), eq(0), eq(chunk));
        verify(listener, times(1)).onUpdateProgress(0, filesize);
        verify(listener, times(1)).onUpdateProgress(chunk, filesize);
        verify(listener, times(1)).onUpdateProgress(filesize, filesize);
    }

    /**
     * Test of writeToStream method, of class JSchScpOperation
     *
     * When throws an IOException
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testWriteToStreamIOException() throws IOException, SshException {
        System.out.println("writeToStream when throws exception");
        //given
        int filesize = 123;
        ioChannel.setChannel(channel);
        when(channel.isConnected()).thenReturn(true);
        when(input.read(any(), eq(0), eq(filesize))).thenReturn(filesize);
        doThrow(IOException.class).when(output).write(any(), eq(0), eq(filesize));
        ioChannel.setStatusListener(listener);

        //when
        ioChannel.writeToStream(output, filesize);

        //then
        verify(listener, times(1)).onUpdateProgress(0, filesize);
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.FILE_WRITE_ERROR);
    }

    /**
     * Test of updateProgress method, of class JSchIOChannel.
     *
     * Where no StatusListener
     */
    @Test
    public void testUpdateProgressNoListener() {
        System.out.println("updateProgress missing StatusListener");
        //given
        ioChannel.setStatusListener(null);

        //when
        ioChannel.updateProgress(0, 1);

        //then
        assertNull(ioChannel.getStatusListener());
        verify(listener, times(0)).onUpdateProgress(0, 1);
    }

    /**
     * Test of updateStatus method, of class JSchIOChannel.
     *
     * Where no StatusListener
     */
    @Test
    public void testUpdateStatusNoListener() {
        System.out.println("updateStatus missing StatusListener");
        //given
        ioChannel.setStatusListener(null);

        //when
        ioChannel.updateStatus(SshOperationStatus.CONNECTED);

        //then
        assertNull(ioChannel.getStatusListener());
        verify(listener, times(0)).onUpdateStatus(SshOperationStatus.CONNECTED);
    }

    /**
     * Test of readFromStream method, of class JSchScpOperation
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(timeout = 100L)
    public void testReadFromStream() throws IOException, SshException {
        System.out.println("readFromStream");
        //given
        int filesize = 100;
        int chunk = 50;
        ioChannel.setChannel(channel);
        ioChannel.setStatusListener(listener);
        when(channel.isConnected()).thenReturn(true);
        when(input.read(any(), eq(0), eq(filesize))).thenReturn(chunk);
        when(input.read(any(), eq(0), eq(filesize - chunk))).thenReturn(chunk);

        //when
        ioChannel.readFromStream(input, filesize);

        //then
        verify(input, times(1)).read(any(), eq(0), eq(filesize));
        verify(input, times(1)).read(any(), eq(0), eq(filesize - chunk));
        verify(output, times(2)).write(any(), eq(0), eq(chunk));
        verify(listener, times(1)).onUpdateProgress(0, filesize);
        verify(listener, times(1)).onUpdateProgress(chunk, filesize);
        verify(listener, times(1)).onUpdateProgress(filesize, filesize);
    }
}
