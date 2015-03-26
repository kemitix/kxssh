package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.kxssh.SshException;

@Setter
@Getter
public class JSchIOChannel {

    Channel channel;
    OutputStream output;
    InputStream input;
    String remoteFilename;
    File localFile;

    public static JSchIOChannel createExecIOChannel(Session session) throws SshException {
        JSchIOChannel ioChannel = new JSchIOChannel();
        try {
            ioChannel.setChannel(session.openChannel("exec"));
        } catch (JSchException ex) {
            Logger.getLogger(JSchSftpClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new SshException("Error opening exec channel", ex);
        } catch (IOException ex) {
            Logger.getLogger(JSchSftpClient.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JSchIOChannel.class.getName()).log(Level.SEVERE, null, ex);
            throw new SshException("Error connecting  channel", ex);
        }
    }

    int read(byte[] buffer, int offset, int length) throws IOException {
        return input.read(buffer, offset, length);
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

}
