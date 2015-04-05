package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshIOFactory;
import net.kemitix.kxssh.SshOperationStatus;
import net.kemitix.kxssh.SshPasswordAuthentication;
import net.kemitix.kxssh.SshStatusListener;
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
public class JSchScpOperationTest {

    private JSchScpOperation operation;
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
    private SshStatusListener listener;
    private Channel channel;
    private OutputStream outputStream;
    private InputStream inputStream;
    private JSchIOChannel ioChannel;

    @Before
    public void setUp() throws JSchException, SshException {
        connectionProperties = mock(SshConnectionProperties.class);
        authentication = mock(SshPasswordAuthentication.class);
        session = mock(Session.class);
        ioFactory = mock(SshIOFactory.class);
        jschFactory = mock(JSchFactory.class);
        jsch = mock(JSch.class);
        listener = mock(SshStatusListener.class);
        channel = mock(Channel.class);
        outputStream = mock(OutputStream.class);
        inputStream = mock(InputStream.class);
        ioChannel = mock(JSchIOChannel.class);

        operation = new JSchScpOperation(connectionProperties) {
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
        when(jschFactory.authenticate(any())).thenReturn(jschFactory);
        when(jschFactory.knownHosts(any())).thenReturn(jschFactory);
        when(jschFactory.build()).thenReturn(jsch);
        when(jsch.getSession(username, hostname)).thenReturn(session);

    }

    /**
     * Test of getSession method, of class JSchScpOperation
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testInitSession() throws SshException, JSchException {
        System.out.println("initSession");
        //given

        //when
        operation.getSession();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(authentication, times(1)).authenticateSession(session);
        verify(session, times(1)).connect();
    }

    /**
     * Test of getSession method, of class JSchScpOperation
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
        operation.getSession();

        //then
    }

    /**
     * Test of getSession method, of class JSchScpOperation
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
        operation.getSession();

        //then
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.HOSTNAME_ERROR);
    }

    /**
     * Test of getSession method, of class JSchScpOperation
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
        operation.getSession();

        //then
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.HOSTNAME_ERROR);
    }

    /**
     * Test of getSession method, of class JSchScpOperation
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
        operation.getSession();

        //then
        verify(jsch, times(1)).getSession(any(), eq(hostname));
        verify(authentication, times(1)).authenticateSession(session);
        verify(session, times(1)).connect();
    }

    /**
     * Test of getSession method, of class JSchScpOperation
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
        operation.getSession();

        //then
        verify(jsch, times(1)).getSession(any(), eq(hostname));
        verify(authentication, times(1)).authenticateSession(session);
        verify(session, times(1)).connect();
    }

    /**
     * Test of getSession method, of class JSchScpOperation
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
        operation.getSession();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.SESSION_ERROR);
    }

    /**
     * Test of getSession method, of class JSchScpOperation
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
        operation.getSession();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(session, times(1)).setPassword(password);
        verify(session, times(1)).connect();
        verify(listener, times(1)).onUpdateStatus(SshErrorStatus.SESSION_ERROR);
    }

    /**
     * Test of disconnect method, of class JSchScpOperation
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
     * Test of getExecIOChannel method, of class JSchScpOperation
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     * @throws java.io.IOException
     */
    @Test
    public void testGetExecIOChannel() throws SshException, JSchException, IOException {
        System.out.println("getExecIOChannel");
        //given
        operation.setIoChannel(null);
        when(session.openChannel("exec")).thenReturn(channel);
        when(channel.getOutputStream()).thenReturn(outputStream);
        when(channel.getInputStream()).thenReturn(inputStream);

        //when
        operation.getExecIOChannel();

        //then
        verify(jsch, times(1)).getSession(username, hostname);
        verify(authentication, times(1)).authenticateSession(session);
        verify(session, times(1)).connect();
    }

    /**
     * Test of getJSch method, of class JSchScpOperation
     *
     * @throws com.jcraft.jsch.JSchException
     * @throws net.kemitix.kxssh.SshException
     */
    @Test(expected = RuntimeException.class)
    public void testGetJSchThrowsException() throws JSchException, SshException {
        System.out.println("getJSch when throws exception");
        //given
        when(jschFactory.build()).thenThrow(JSchException.class);

        //when
        operation.getExecIOChannel();

        //then
    }

    /**
     * Test of updateProgress method, of class JSchIOChannel.
     */
    @Test
    public void testUpdateProgress() {
        System.out.println("updateProgress");
        //given
        operation.setStatusListener(listener);

        //when
        operation.updateProgress(0, 1);

        //then
        verify(listener, times(1)).onUpdateProgress(0, 1);
    }

    /**
     * Test of updateStatus method, of class JSchIOChannel.
     */
    @Test
    public void testUpdateStatus() {
        System.out.println("updateStatus");
        //given
        operation.setStatusListener(listener);

        //when
        operation.updateStatus(SshOperationStatus.CONNECTED);

        //then
        verify(listener, times(1)).onUpdateStatus(SshOperationStatus.CONNECTED);
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
        operation.setStatusListener(null);

        //when
        operation.updateProgress(0, 1);

        //then
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
        operation.setStatusListener(null);

        //when
        operation.updateStatus(SshOperationStatus.CONNECTED);

        //then
        verify(listener, times(0)).onUpdateStatus(SshOperationStatus.CONNECTED);
    }
}
