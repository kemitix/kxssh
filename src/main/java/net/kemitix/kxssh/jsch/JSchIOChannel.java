package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.kxssh.SshException;

@Setter
@Getter
public class JSchIOChannel {

    private Channel channel;
    private OutputStream output;
    private InputStream input;
    private String remoteFilename;
    private File localFile;

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

    public void setRemoteFilename(String remoteFilename) {
        this.remoteFilename = remoteFilename;
        setExecCommand("scp -f " + remoteFilename);
    }

    private void setChannel(Channel sessionChannel) throws IOException {
        this.channel = sessionChannel;
        setOutput(sessionChannel.getOutputStream());
        setInput(sessionChannel.getInputStream());
    }

    private void setExecCommand(String remoteCommand) {
        ((ChannelExec) channel).setCommand(remoteCommand);
    }

    public void connect() throws SshException {
        try {
            channel.connect();
        } catch (JSchException ex) {
            throw new SshException("Error connecting channel", ex);
        }
    }

    // READ, WRITE & FLUSH
    private static final String ERROR_FILE_REMOTE_READ = "Error reading remote file";
    private static final String ERROR_READ_OVERRUN = "Error tried to read past end of file";

    int read(byte[] buffer, int offset, int length) throws SshException {
        int bytesRead;
        try {
            bytesRead = input.read(buffer, offset, length);
        } catch (IOException ex) {
            throw new SshException(ERROR_FILE_REMOTE_READ, ex);
        }
        if (bytesRead == -1) {
            throw new SshException(ERROR_READ_OVERRUN);
        }
        return bytesRead;
    }

    int read() throws IOException {
        return input.read();
    }

    void write(byte[] buffer, int offset, int length) throws IOException {
        output.write(buffer, offset, length);
    }

    void flush() throws IOException {
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
        try {
            int status = read();
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

    private String readToEol() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        do {
            c = read();
            sb.append((char) c);
        } while (c != '\n');
        return sb.toString();
    }

    // NOTIFY READY
    private static final String ERROR_REMOTE_NOTIFY = "Error writing/flushing null on output stream";

    protected void notifyReady() throws SshException {
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
     * @return the file size
     * @throws SshException
     */
    protected IOChannelMetadata readMetaData() throws SshException {
        IOChannelMetadata metadata = new IOChannelMetadata();
        byte[] header = new byte[6];
        byte[] buffer = new byte[1024]; // needs to be able to hold a filename

        // read 5-byte header
        read(header, 0, 5);
        metadata.setHeader(header);

        // read filesize, as a string, terminated by a space
        StringBuilder sb = new StringBuilder();
        while (true) {
            read(buffer, 0, 1);
            if (buffer[0] == ' ') {
                break;
            }
            sb.append(buffer[0] - '0');
        }
        metadata.setFilesize(Integer.parseInt(sb.toString()));

        /**
         * Continue reading to remove the filename from the channel, terminated
         * by a line feed (ascii hex 0a). Although we don't do anything file the
         * filename, we still needed to remove it from the channel.
         */
        for (int i = 0;; i++) {
            read(buffer, i, 1);
            if (buffer[i] == (byte) 0x0a) {
                metadata.setFilename(Arrays.toString(buffer).substring(0, i));
                break;
            }
        }

        return metadata;
    }

}
