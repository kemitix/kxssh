package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.kemitix.kxssh.SshException;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchIOChannelTest {

    private JSchIOChannel ioChannel;

    private Channel channel;
    private OutputStream output;
    private InputStream input;
    private String remoteFilename;
    private File localFile;

    @Before
    public void setUp() throws IOException {
        ioChannel = new JSchIOChannel();

        channel = mock(Channel.class);
        output = mock(OutputStream.class);
        input = mock(InputStream.class);
        remoteFilename = "remote.txt";
        localFile = mock(File.class);

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
     * Test of read(byte[], int, int) method, of class JSchIOChannel.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test
    public void testRead() throws SshException, IOException {
        System.out.println("read(byte[], int, int)");
        //given
        ioChannel.setInput(input);
        byte[] buffer = new byte[10];
        int offset = 0;
        int length = 10;
        when(input.read(buffer, offset, length)).thenReturn(length);

        //when
        int result = ioChannel.read(buffer, offset, length);

        //then
        assertThat(result, is(length));
    }

    /**
     * Test of read(byte[], int, int) method, of class JSchIOChannel.
     *
     * Throws IOException.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(expected = SshException.class)
    public void testReadIOException() throws SshException, IOException {
        System.out.println("read(byte[], int, int) throws IOException");
        //given
        ioChannel.setInput(input);
        byte[] buffer = new byte[10];
        int offset = 0;
        int length = 10;
        when(input.read(buffer, offset, length)).thenThrow(IOException.class);

        //when
        ioChannel.read(buffer, offset, length);

        //then
    }

    /**
     * Test of read(byte[], int, int) method, of class JSchIOChannel.
     *
     * Reach end of stream.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws java.io.IOException
     */
    @Test(expected = SshException.class)
    public void testReadEndOfStream() throws SshException, IOException {
        System.out.println("read(byte[], int, int) reach end of stream");
        //given
        ioChannel.setInput(input);
        byte[] buffer = new byte[10];
        int offset = 0;
        int length = 10;
        when(input.read(buffer, offset, length)).thenReturn(-1);

        //when
        ioChannel.read(buffer, offset, length);

        //then
    }

}
