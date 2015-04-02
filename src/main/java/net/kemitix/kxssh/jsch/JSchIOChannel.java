package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.kxssh.IOChannelReadReply;
import net.kemitix.kxssh.IOChannelReadReplyFactory;
import net.kemitix.kxssh.SshErrorStatus;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshStatus;
import net.kemitix.kxssh.SshStatusListener;
import net.kemitix.kxssh.SshStatusProvider;
import net.kemitix.kxssh.scp.ScpCommand;

@Setter
@Getter
public class JSchIOChannel implements SshStatusProvider {

    private Channel channel;
    private OutputStream output;
    private InputStream input;
    private File localFile;

    public JSchIOChannel() {
        readReplyFactory = new IOChannelReadReplyFactory();
    }

    public static JSchIOChannel createExecIOChannel(Session session) throws SshException {
        JSchIOChannel ioChannel = new JSchIOChannel();
        try {
            ioChannel.setChannel(session.openChannel("exec"));
        } catch (JSchException ex) {
            throw new SshException("Error opening exec channel", ex);
        } catch (IOException ex) {
            throw new SshException("Error getting Input/Output Streams from channel", ex);
        }
        return ioChannel;
    }

    protected void setChannel(Channel channel) throws IOException {
        this.channel = channel;
        if (channel == null) {
            output = null;
            input = null;
        } else {
            output = channel.getOutputStream();
            input = channel.getInputStream();
        }
    }

    public void setExecCommand(String remoteCommand) throws SshException {
        ((ChannelExec) channel).setCommand(remoteCommand);
    }

    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }

    public void connect() throws SshException {
        if (!isConnected()) {
            try {
                channel.connect();
            } catch (JSchException ex) {
                throw new SshException("Error connecting channel", ex);
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            channel.disconnect();
        }
    }

    private void requireConnection() throws SshException {
        if (!isConnected()) {
            throw new SshException("Not connected to channel");
        }
    }

    // READ, WRITE & FLUSH
    private static final String ERROR_FILE_REMOTE_READ = "Error reading remote file";
    private static final String ERROR_READ_EOF = "Error tried to read past end of file";

    private IOChannelReadReplyFactory readReplyFactory;

    IOChannelReadReply read(int length) throws SshException {
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

    void write(byte[] buffer, int offset, int length) throws IOException, SshException {
        requireConnection();
        output.write(buffer, offset, length);
    }

    void flush() throws IOException, SshException {
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

    protected int checkStatus() throws SshException {
        requireConnection();
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

    public String readToEol() throws IOException, SshException {
        requireConnection();
        StringBuilder sb = new StringBuilder();
        int c;
        do {
            c = input.read();
            sb.append((char) c);
        } while (c != '\n');
        return sb.toString();
    }

    // NOTIFY READY
    private static final String ERROR_REMOTE_NOTIFY = "Error writing/flushing null on output stream";

    protected void notifyReady() throws SshException {
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
     * @throws java.io.IOException
     * @throws SshException
     */
    protected ScpCommand readScpCommand() throws IOException, SshException {
        String commandLine = readToEol();
        ScpCommand scpCommand = ScpCommand.parse(commandLine);
        return scpCommand;
    }

    // WRITE STREAM
    void writeToStream(OutputStream stream, long length) throws SshException {
        int blockSize = 1024;
        long remaining = length;
        updateProgress(0, length);
        do {
            int bytesToRead = Integer.min(blockSize, (int) Long.min(remaining, (long) Integer.MAX_VALUE));
            IOChannelReadReply reply = read(bytesToRead);
            int bytesRead = reply.getBytesRead();
            bytesRead = Integer.min(bytesRead, bytesToRead);
            try {
                stream.write(reply.getBuffer(), 0, bytesRead);
            } catch (IOException ex) {
                updateStatus(SshErrorStatus.FILE_WRITE_ERROR);
                throw new SshException("Error writing local file", ex);
            }
            remaining -= bytesRead;
            updateProgress(length - remaining, length);
        } while (remaining > 0);
        updateProgress(length, length);
    }

    // STATUS PROVIDER
    private SshStatusListener statusListener;

    @Override
    public void setStatusListener(SshStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public void updateProgress(long progress, long total) {
        if (statusListener != null) {
            statusListener.onUpdateProgress(progress, total);
        }
    }

    @Override
    public void updateStatus(SshStatus status) {
        if (statusListener != null) {
            statusListener.onUpdateStatus(status);
        }
    }

}
