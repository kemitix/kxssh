package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kemitix.kxssh.SftpClient;
import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshConnectionProperties;
import net.kemitix.kxssh.SshException;
import net.kemitix.kxssh.SshPasswordAuthentication;

public class JSchSftpClient implements SftpClient {

    private static final String SSHKNOWN_HOSTS = "~/.ssh/known_hosts";

    private static final String ERROR_OPENING_LOCAL_FILE_FOR_WRITING = "Error opening local file for writing";
    private static final String ERROR_CLOSING_LOCAL_FILE = "Error closing local file";
    private static final String ERROR_IN_ACK = "Error in ACK";
    private static final String ERROR_READING_REMOTE_FILE = "Error reading remote file";
    private static final String ERROR_WRITING_LOCAL_FILE = "Error writing local file";
    private static final String ERROR_READING_ON_INPUT_STREAM = "Error reading initial metadata from input stream";
    private static final String ERROR_WRITING_ON_OUTPUT_STREAM = "Error writing/flushing null on output stream";
    private static final String ERROR_ACK = "ACK Error: ";
    private static final String ERROR_ACK_FATAL = "Fatal ACK Error: ";
    private static final String ERROR_READING_ACK = "Error reading ACK";
    private static final String ERROR_USERNAME_NOT_SET = "Error username not set";
    private static final String ERROR_HOST_NOT_SET = "Error host not set";
    private static final String ERROR_CREATING_SESSION = "Error creating session";
    private static final String ERROR_CONNECTING_SESSION = "Error connecting session";

    private JSch jsch = null;

    private final SshConnectionProperties connectionProperties;

    public JSchSftpClient(SshConnectionProperties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    int checkAck(JSchIOChannel ioChannel) throws SshException {
        int status;
        try {
            status = ioChannel.read();
            // b may be 0 for success,
            //          1 for error,
            //          2 for fatal error,
            //          -1
            if (status == 0) {
                return status;
            }
            if (status == -1) {
                return status;
            }

            if (status == 1 || status == 2) {
                StringBuilder sb = new StringBuilder();
                int c;
                do {
                    c = ioChannel.read();
                    sb.append((char) c);
                } while (c != '\n');
                if (status == 1) { // error
                    System.out.print(sb.toString());
                    setStatus(ERROR_ACK + sb.toString());
                    throw new SshException(ERROR_ACK + sb.toString());
                }
                if (status == 2) { // fatal error
                    System.out.print(sb.toString());
                    setStatus(ERROR_ACK_FATAL + sb.toString());
                    throw new SshException(ERROR_ACK_FATAL + sb.toString());
                }
            }
        } catch (IOException ex) {
            setStatus(ERROR_READING_ACK);
            throw new SshException(ERROR_READING_ACK, ex);
        }
        return status;
    }

    @Override
    public void download(String remoteFilename, String localFilename) throws SshException {
        setStatus("Starting Session...");
        Session session = getSession();
        JSchIOChannel ioChannel = JSchIOChannel.createExecIOChannel(session);
        ioChannel.setRemoteFilename(remoteFilename);
        final File localFile = new File(localFilename);
        ioChannel.setLocalFile(localFile);
        setStatus("Connecting...");
        ioChannel.connect();
        notifyRemoteReady(ioChannel);
        setStatus("Downloading...");
        while (true) {
            int c = checkAck(ioChannel);
            if (c != 'C') {
                break;
            }
            int filesize = readMetaData(ioChannel);
            notifyRemoteReady(ioChannel);
            try {
                FileOutputStream fos = new FileOutputStream(localFile);
                writeIOChannelToOutputStream(ioChannel, fos, filesize);
                fos.close();
            } catch (FileNotFoundException ex) {
                setStatus(ERROR_OPENING_LOCAL_FILE_FOR_WRITING);
                throw new SshException(ERROR_OPENING_LOCAL_FILE_FOR_WRITING, ex);
            } catch (IOException ex) {
                setStatus(ERROR_CLOSING_LOCAL_FILE);
                throw new SshException(ERROR_CLOSING_LOCAL_FILE, ex);
            }
            if (checkAck(ioChannel) != 0) {
                setStatus(ERROR_IN_ACK);
                throw new SshException(ERROR_IN_ACK);
            }
            notifyRemoteReady(ioChannel);
        }
        setStatus("Disconnecting...");
        session.disconnect();
        setStatus("Downloaded");
    }

    private void writeIOChannelToOutputStream(
            JSchIOChannel ioChannel,
            FileOutputStream fos,
            int filesize)
            throws SshException {
        byte[] buf = new byte[1024];
        int remaining = filesize;
        /**
         * loop over buf.length sized blocks of input
         */
        while (true) {
            int bytesToRead;
            if (buf.length < remaining) {
                bytesToRead = buf.length;
            } else {
                bytesToRead = remaining;
            }
            int bytesRead;
            try {
                bytesRead = ioChannel.read(buf, 0, bytesToRead);
            } catch (IOException ex) {
                setStatus(ERROR_READING_REMOTE_FILE);
                throw new SshException(ERROR_READING_REMOTE_FILE, ex);
            }
            if (bytesRead < 0) {
                // error
                break;
            }
            // prevent overrun
            if (bytesRead > remaining) {
                bytesRead = remaining;
            }
            try {
                fos.write(buf, 0, bytesRead);
            } catch (IOException ex) {
                setStatus(ERROR_WRITING_LOCAL_FILE);
                throw new SshException(ERROR_WRITING_LOCAL_FILE, ex);
            }
            remaining -= bytesRead;
            if (remaining == 0) {
                break;
            }
            if (remaining < 0) {
                setStatus("Overrun!");
                break;
            }
            notifyProgress(remaining, filesize);
        }
        notifyProgress(remaining < 0 ? 0 : remaining, filesize);
    }

    private int readMetaData(JSchIOChannel ioChannel) throws SshException {
        byte[] buf = new byte[1024];
        int filesize = 0;
        try {
            // read file unix permissions, 4 chars with a space terminator
            // e.g. '0644 '
            ioChannel.read(buf, 0, 5);

            // read filesize as a string, terminated by a null or space
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (ioChannel.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }
                if (buf[0] == ' ') {
                    break;
                }
                sb.append(buf[0] - '0');
            }
            filesize = Integer.parseInt(sb.toString());

            // read filename (?), terminated by 0x0a
            String file = null;
            for (int i = 0;; i++) {
                ioChannel.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }
        } catch (IOException ex) {
            setStatus(ERROR_READING_ON_INPUT_STREAM);
            throw new SshException(ERROR_READING_ON_INPUT_STREAM, ex);
        }
        return filesize;
    }

    private void notifyRemoteReady(JSchIOChannel ioChannel) throws SshException {
        byte[] buf = new byte[1];
        // send '\0'
        buf[0] = 0;
        try {
            ioChannel.write(buf, 0, 1);
            ioChannel.flush();
        } catch (IOException ex) {
            setStatus(ERROR_WRITING_ON_OUTPUT_STREAM);
            throw new SshException(ERROR_WRITING_ON_OUTPUT_STREAM, ex);
        }
    }

    private Session getSession() throws SshException {
        SshAuthentication authentication = connectionProperties.getAuthentication();
        if (jsch == null) {
            jsch = new JSch();
            try {
                jsch.setKnownHosts(SSHKNOWN_HOSTS);
            } catch (JSchException ex) {
                setStatus(SSHKNOWN_HOSTS + " not found");
                throw new RuntimeException(SSHKNOWN_HOSTS + " not found");
            }
        }
        if (authentication.getUsername() == null) {
            setStatus(ERROR_USERNAME_NOT_SET);
            throw new RuntimeException(ERROR_USERNAME_NOT_SET);
        }
        if (connectionProperties.getHostname() == null) {
            setStatus(ERROR_HOST_NOT_SET);
            throw new RuntimeException(ERROR_HOST_NOT_SET);
        }

        Session session;
        try {
            session = jsch.getSession(authentication.getUsername(), connectionProperties.getHostname());
        } catch (JSchException ex) {
            setStatus(ERROR_CREATING_SESSION);
            throw new SshException(ERROR_CREATING_SESSION, ex);
        }

        session.setPassword(((SshPasswordAuthentication) authentication).getPassword().getBytes());

        try {
            session.connect();
        } catch (JSchException ex) {
            if (ex.getMessage().contains("UnknownHostKey")) {
                Logger.getLogger(this.getClass().getName())
                        .log(Level.SEVERE, "Try adding key with: ssh-keyscan -t rsa {0} >> {1}", new Object[]{
                            connectionProperties.getHostname(), SSHKNOWN_HOSTS
                        });
            }
            setStatus(ERROR_CONNECTING_SESSION);
            throw new SshException(ERROR_CONNECTING_SESSION, ex);
        }

        return session;
    }

    private void notifyProgress(int remaining, int filesize) {
//        if (listener != null) {
//            listener.updateCounter(filesize - remaining, 0, filesize);
//        }
    }

    private void setStatus(String message) {
//        if (listener != null) {
//            listener.updateStatus(message);
//        }
    }

}
