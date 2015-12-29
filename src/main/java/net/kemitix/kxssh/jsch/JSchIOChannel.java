package net.kemitix.kxssh.jsch;

import net.kemitix.kxssh.scp.ScpCommandFactory;
import net.kemitix.kxssh.ssh.IOChannelReadReply;
import net.kemitix.kxssh.ssh.IOChannelReadReplyFactory;
import net.kemitix.kxssh.ssh.SshErrorStatus;
import net.kemitix.kxssh.ssh.SshException;
import net.kemitix.kxssh.ssh.SshStatus;
import net.kemitix.kxssh.ssh.SshStatusListener;
import net.kemitix.kxssh.ssh.SshStatusProvider;
import net.kemitix.kxssh.scp.ScpCommand;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Implements an IO channel over SSH using JSCH.
 *
 * @author pcampbell
 */
@Setter
@Getter
@SuppressWarnings("unused")
public class JSchIOChannel implements SshStatusProvider {

    private Channel channel;
    private OutputStream output;
    private InputStream input;
    private File localFile;

    private static final int BLOCK_SIZE = 4096;

    /**
     * Constructor.
     */
    public JSchIOChannel() {
        readReplyFactory = new IOChannelReadReplyFactory();
    }

    /**
     * Creates a JSCH IO Channel for sending commands for the {@link Session}.
     *
     * @param session the session for the IO channel
     *
     * @return the JSCH IO Channel
     */
    public static JSchIOChannel createExecIOChannel(final Session session) {
        JSchIOChannel ioChannel = new JSchIOChannel();
        try {
            ioChannel.setChannel(session.openChannel("exec"));
        } catch (JSchException ex) {
            throw new SshException("Error opening exec channel", ex);
        } catch (IOException ex) {
            throw new SshException(
                    "Error getting Input/Output Streams from channel", ex);
        }
        return ioChannel;
    }

    /**
     * Sets the {@link Channel}.
     *
     * @param channel the channel
     *
     * @throws IOException if there is an error getting either of the IO streams
     *                     from the channel
     */
    @SuppressWarnings("hiddenfield")
    protected void setChannel(final Channel channel) throws IOException {
        this.channel = channel;
        if (channel == null) {
            output = null;
            input = null;
        } else {
            output = channel.getOutputStream();
            input = channel.getInputStream();
        }
    }

    /**
     * Sets the command to be executed.
     *
     * @param remoteCommand the command
     */
    public void setExecCommand(final String remoteCommand) {
        ((ChannelExec) channel).setCommand(remoteCommand);
    }

    /**
     * Checks if the IO channel is connected.
     *
     * @return true if connected
     */
    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }

    /**
     * Attempts to connect the IO channel.
     */
    public void connect() {
        if (!isConnected()) {
            try {
                channel.connect();
            } catch (JSchException ex) {
                throw new SshException("Error connecting channel", ex);
            }
        }
    }

    /**
     * Disconnects the IO channel.
     */
    public void disconnect() {
        if (isConnected()) {
            channel.disconnect();
        }
    }

    /**
     * Ensures that the IO channel is connected and throws an
     * {@link SshException} if it is not.
     */
    private void requireConnection() {
        if (!isConnected()) {
            throw new SshException("Not connected to channel");
        }
    }

    // READ, WRITE & FLUSH
    private static final String ERROR_FILE_REMOTE_READ
            = "Error reading remote file";
    private static final String ERROR_READ_EOF
            = "Error tried to read past end of file";

    private IOChannelReadReplyFactory readReplyFactory;

    /**
     * Reads a number of bytes from the IO channel's input stream.
     *
     * @param length the number of bytes to read
     *
     * @return a read reply object containing the status of the read and the
     *         bytes, if any, read
     */
    public IOChannelReadReply read(final int length) {
        requireConnection();
        byte[] buffer = new byte[length];
        int bytesRead;
        try {
            bytesRead = input.read(buffer, 0, length);
            if (bytesRead == EOF) {
                throw new SshException(ERROR_READ_EOF);
            }
            return readReplyFactory.createReply(length, bytesRead, buffer);
        } catch (IOException ex) {
            throw new SshException(ERROR_FILE_REMOTE_READ, ex);
        }
    }

    /**
     * Writes the buffer to the IO channel's output stream.
     *
     * @param buffer the buffer to be written
     * @param offset where to start from in the buffer
     * @param length the number of bytes to be written
     *
     * @throws IOException if there is an error writing to the channel
     */
    void write(final byte[] buffer, final int offset, final int length)
            throws IOException {
        requireConnection();
        output.write(buffer, offset, length);
    }

    /**
     * Flushes the IO channel's output stream.
     *
     * @throws IOException if there is an error on the channel
     */
    void flush() throws IOException {
        requireConnection();
        output.flush();
    }

    // STATUS
    private static final String ERROR_STATUS = "Status Error: ";
    private static final String ERROR_READING_STATUS = "Error reading status";

    public static final int EOF = -1;
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int FATAL = 2;
    public static final int CONTINUE = 'C';

    /**
     * Reads the status from the IO channel's input stream.
     *
     * @return the status
     */
    protected int checkStatus() {
        try {
            int status = input.read();
            switch (status) {
                case ERROR:
                case FATAL:
                    throw new SshException(ERROR_STATUS + readToEol());
                case EOF:
                case SUCCESS:
                default:
                    return status;
            }
        } catch (IOException ex) {
            throw new SshException(ERROR_READING_STATUS, ex);
        }
    }

    /**
     * Reads from the IO channel's input stream until an EOL (i.e. newline)
     * character is found.
     *
     * @return the line read
     *
     * @throws IOException if there is an error reading from the input stream
     */
    public String readToEol() throws IOException {
        return readToEol('\n');
    }

    /**
     * Reads from the IO channel's input stream until the terminator character
     * is found.
     *
     * @param terminator the character to stop reading when found
     *
     * @return the line read
     *
     * @throws IOException if there is an error reading from the input stream
     */
    public String readToEol(final char terminator) throws IOException {
        requireConnection();
        StringBuilder sb = new StringBuilder();
        int c;
        do {
            c = input.read();
            sb.append((char) c);
        } while (c != terminator);
        return sb.toString();
    }

    // NOTIFY READY
    private static final String ERROR_REMOTE_NOTIFY
            = "Error writing/flushing null on output stream";

    /**
     * Notify the remote server that we are ready.
     */
    protected void notifyReady() {
        requireConnection();
        byte[] buf = new byte[1];
        buf[0] = 0; // send '\0' - null
        try {
            write(buf, 0, 1);
            flush();
        } catch (IOException ex) {
            throw new SshException(ERROR_REMOTE_NOTIFY, ex);
        }
    }

    // METADATA
    /**
     * Read metadata, which consists of the file size followed by the filename.
     *
     * @return the scp protocol command
     *
     * @throws IOException if there is an error reading or parsing the command
     */
    protected ScpCommand readScpCommand() throws IOException {
        String commandLine = readToEol(ScpCommand.TERMINATOR);
        ScpCommand scpCommand = ScpCommandFactory.parse(commandLine);
        return scpCommand;
    }

    // WRITE STREAM
    /**
     * Write the number of bytes from the IO channel's input stream to the
     * supplied {@link OutputStream}.
     *
     * @param stream the stream to write to
     * @param length the number of bytes to write
     */
    void writeToStream(final OutputStream stream, final long length) {
        long remaining = length;
        updateProgress(0, length);
        do {
            int bytesToRead = Integer.min(BLOCK_SIZE,
                    (int) Long.min(remaining, (long) Integer.MAX_VALUE));
            IOChannelReadReply reply = read(bytesToRead);
            int bytesRead = reply.getBytesRead();
            bytesRead = Integer.min(bytesRead, bytesToRead);
            try {
                stream.write(reply.getBuffer().getBytes(UTF_8), 0, bytesRead);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.FILE_WRITE_ERROR);
                throw new SshException("Error writing local file", ex);
            }
            remaining -= bytesRead;
            updateProgress(length - remaining, length);
        } while (remaining > 0);
    }

    // READ STREAM
    /**
     * Read the number of bytes from the supplied {@link InputStream} and write
     * them to the IO channel's output stream.
     *
     * @param stream the stream to read from
     * @param length the number of bytes to read
     *
     * @throws IOException if there is an error reading from the supplied input
     *                     stream of writing to the output stream
     */
    void readFromStream(final InputStream stream, final long length)
            throws IOException {
        byte[] buffer = new byte[BLOCK_SIZE];
        long remaining = length;
        updateProgress(0, length);
        do {
            int bytesToRead = Integer.min(BLOCK_SIZE,
                    (int) Long.min(Integer.MAX_VALUE, remaining));
            int bytesRead = stream.read(buffer, 0, bytesToRead);
            output.write(buffer, 0, bytesRead);
            output.flush();
            remaining -= bytesRead;
            updateProgress(length - remaining, length);
        } while (remaining > 0);
    }

    // STATUS PROVIDER
    private SshStatusListener statusListener;

    @Override
    public void setStatusListener(final SshStatusListener listener) {
        statusListener = listener;
    }

    @Override
    public void updateProgress(final long progress, final long total) {
        if (statusListener != null) {
            statusListener.onUpdateProgress(progress, total);
        }
    }

    @Override
    public void updateStatus(final SshStatus status) {
        if (statusListener != null) {
            statusListener.onUpdateStatus(status);
        }
    }

}
