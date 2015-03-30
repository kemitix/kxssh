package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.OutputStream;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshIOFactory;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.StatusListener;
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
public class JSchOperationTest {

    private JSchOperation operation;
    private SshConnectionProperties connectionProperties;
    private SshPasswordAuthentication authentication;
    private String knownHosts;
    private String hostname;
    private String username;
    private String password;
    private Session session;
    private SshIOFactory ioFactory;
    private JSchFactory jschFactory;
    private JSch jsch;
    private StatusListener listener;
    private Channel channel;
    private OutputStream outputStream;
    private JSchIOChannel ioChannel;

    @Before
    public void setUp() throws JSchException {
        connectionProperties = mock(SshConnectionProperties.class);
        authentication = mock(SshPasswordAuthentication.class);
        session = mock(Session.class);
        ioFactory = mock(SshIOFactory.class);
        jschFactory = mock(JSchFactory.class);
        jsch = mock(JSch.class);
        listener = mock(StatusListener.class);
        channel = mock(Channel.class);
        outputStream = mock(OutputStream.class);
        ioChannel = mock(JSchIOChannel.class);

        operation = new JSchOperation(connectionProperties) {
        };

        knownHosts = "src/test/resources/known_hosts";
        hostname = "localhost";
        username = "kemitix";
        password = "secret";

        operation.setStatusListener(listener);
        operation.setKnownHosts(knownHosts);
        operation.setJschFactory(jschFactory);
        operation.setIoFactory(ioFactory);

        when(connectionProperties.getAuthentication()).thenReturn(authentication);
        when(authentication.getUsername()).thenReturn(username);
        when(authentication.getPassword()).thenReturn(password);
        when(connectionProperties.getHostname()).thenReturn(hostname);
        when(session.openChannel("exec")).thenReturn(channel);
        when(jschFactory.build(any())).thenReturn(jsch);
        when(jsch.getSession(username, hostname)).thenReturn(session);

    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testInitSession() throws SshException, JSchException {
        System.out.println("initSession");
        //given

        //when
        operation.initSession();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * When session already exists
     *
     * @throws net.kemitix.kxssh.SshException
     */
    @Test
    public void testInitSessionSessionExists() throws SshException {
        System.out.println("initSession when session exists");
        //given
        operation.setSession(session);

        //when
        operation.initSession();

        //then
    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * When hostname is blank.
     *
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class)
    public void testInitSessionBlankHostname() throws SshException {
        System.out.println("initSession with blank hostname");
        //given
        when(connectionProperties.getHostname()).thenReturn("");

        //when
        operation.initSession();

        //then
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.HOSTNAME_ERROR);
    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * When hostname is null.
     *
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class)
    public void testInitSessionNullHostname() throws SshException {
        System.out.println("initSession with null hostname");
        //given
        when(connectionProperties.getHostname()).thenReturn(null);

        //when
        operation.initSession();

        //then
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.HOSTNAME_ERROR);
    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * When username is blank.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testInitSessionBlankUsername() throws SshException, JSchException {
        System.out.println("initSession with blank username");
        //given
        when(jsch.getSession(any(), eq(hostname))).thenReturn(session);
        when(authentication.getUsername()).thenReturn("");

        //when
        operation.initSession();

        //then
        verify(jsch, times(1)).getSession(any(), eq(hostname));
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * When username is null.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testInitSessionNullUsername() throws SshException, JSchException {
        System.out.println("initSession with null username");
        //given
        when(jsch.getSession(any(), eq(hostname))).thenReturn(session);
        when(authentication.getUsername()).thenReturn(null);

        //when
        operation.initSession();

        //then
        verify(jsch, times(1)).getSession(any(), eq(hostname));
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * Throw JSchException for an Unknown Host Key on session.connect()
     *
     * @throws com.jcraft.jsch.JSchException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class)
    public void testInitSessionJSchExceptionUnknownHostKey() throws JSchException, SshException {
        System.out.println("initSession w/JSchException for unknown host key on session.connect()");
        //given
        doThrow(new JSchException("UnknownHostKey")).when(session).connect();

        //when
        operation.initSession();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.SESSION_ERROR);
    }

    /**
     * Test of initSession method, of class JSchOperation
     *
     * Throw JSchException on session.connect()
     *
     * @throws com.jcraft.jsch.JSchException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class)
    public void testInitSessionJSchException() throws JSchException, SshException {
        System.out.println("initSession w/JSchException on session.connect()");
        //given
        doThrow(new JSchException("another message")).when(session).connect();

        //when
        operation.initSession();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.SESSION_ERROR);
    }

    /**
     * Test of releaseIOChannel method, of class JSchOperation
     */
    @Test
    public void testReleaseIOChannel() {
        System.out.println("releaseIOChannel");
        //given
        operation.setIoChannel(null);

        //when
        operation.releaseIOChannel();

        //then
    }

    /**
     * Test of disconnect method, of class JSchOperation
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        //given
        operation.setSession(null);

        //when
        operation.disconnect();

        //then
    }

    /**
     * Test of updateProgress method, of class JSchOperation
     */
    @Test
    public void testUpdateProgress() {
        System.out.println("updateProgress without listener");
        //given
        operation.setStatusListener(null);

        //when
        operation.updateProgress(1, 2);

        //then
    }

    /**
     * Test of getExecIOChannel method, of class JSchOperation
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testGetExecIOChannel() throws SshException, JSchException {
        System.out.println("getExecIOChannel");
        //given
        operation.setIoChannel(null);

        //when
        operation.getExecIOChannel();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
    }

    /**
     * Test of getJSch method, of class JSchOperation
     *
     * @throws com.jcraft.jsch.JSchException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = RuntimeException.class)
    public void testGetJSchThrowsException() throws JSchException, SshException {
        System.out.println("getJSch when throws exception");
        //given
        when(jschFactory.build(knownHosts)).thenThrow(JSchException.class);

        //when
        operation.getExecIOChannel();

        //then
    }

    /**
     * Test of writeIOChannelToOutputStream method, of class JSchOperation
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(timeout = 1000L)
    public void testWriteIOChannelToOutputStream() throws IOException, SshException {
        System.out.println("writeIOChannelToOutputStream");
        //given
        int filesize = 100;
        int chunk = 50;
        IOChannelReadReply reply1 = mock(IOChannelReadReply.class);
        when(reply1.getBytesRead()).thenReturn(chunk);
        IOChannelReadReply reply2 = mock(IOChannelReadReply.class);
        when(reply2.getBytesRead()).thenReturn(chunk);
        when(ioChannel.read(eq(filesize))).thenReturn(reply1);
        when(ioChannel.read(eq(filesize - chunk))).thenReturn(reply2);

        //when
        operation.writeIOChannelToOutputStream(ioChannel, outputStream, filesize);

        //then
        verify(listener, times(1)).onUpdateProgress(chunk, filesize);
        verify(listener, times(2)).onUpdateProgress(filesize, filesize);
    }

    /**
     * Test of writeIOChannelToOutputStream method, of class JSchOperation
     *
     * When throws an IOException
     *
     * @throws java.io.IOException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = SshException.class, timeout = 100L)
    public void testWriteIOChannelToOutputStreamThrowsException() throws IOException, SshException {
        System.out.println("writeIOChannelToOutputStream when throws exception");
        //given
        int filesize = 123;
        IOChannelReadReply reply = mock(IOChannelReadReply.class);
        when(reply.getBytesRead()).thenReturn(filesize);
        when(ioChannel.read(eq(filesize))).thenReturn(reply);
        doThrow(IOException.class).when(outputStream).write(any(), eq(0), eq(filesize));

        //when
        operation.writeIOChannelToOutputStream(ioChannel, outputStream, filesize);

        //then
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.FILE_WRITE_ERROR);
    }

    /**
     * Test of updateStatus method, of class JSchOperation
     *
     * Where status listener not set
     */
    @Test
    public void testUpdateStatus() {
        System.out.println("updateStatus with no listener set");
        //given
        operation.setStatusListener(null);

        //when
        operation.updateStatus(SshOperationStatus.CONNECTED);

        //then
        verify(listener, times(0)).onUpdateStatus(any());
    }
}
