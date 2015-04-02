package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ScpTransferCommand extends ScpCommand {

    private final byte[] fileMode = new byte[4];
    private long length;
    private String name;

    public ScpTransferCommand() {
    }

    public ScpTransferCommand(String commandLine) throws UnsupportedEncodingException {
        parseCommandLine(commandLine);
    }

    public void setFileMode(byte[] fileMode) {
        if (fileMode.length != 4) {
            throw new IllegalArgumentException("File mode must be 4-byte array");
        }
        System.arraycopy(fileMode, 0, this.fileMode, 0, 4);
    }

    protected abstract String getCommandPattern();

    private void parseCommandLine(String commandLine) throws UnsupportedEncodingException {
        // parse "mmmm length filename"
        Matcher matcher
                = Pattern
                .compile(getCommandPattern())
                .matcher(commandLine);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal command format: " + commandLine);
        }
        System.arraycopy(matcher.group("mode").getBytes("UTF-8"), 0, fileMode, 0, 4);
        length = Long.parseLong(matcher.group("length"));
        name = matcher.group("name");
    }

    @Override
    public byte[] getBytes() throws UnsupportedEncodingException {
        String lengthString = Long.toString(getLength());

        int bufferSize = 8 // command(1), fileMode(4), delimiters(2), terminator(1)
                + lengthString.length()
                + getName().length();

        byte[] buffer = new byte[bufferSize];

        if (this instanceof ScpCopyCommand) {
            buffer[0] = 'C';
        } else {
            buffer[0] = 'D';
        }

        System.arraycopy(getFileMode(), 0, buffer, 1, 4);
        buffer[5] = ' ';
        System.arraycopy(lengthString.getBytes("UTF-8"), 0, buffer, 6, lengthString.length());
        buffer[6 + lengthString.length()] = ' ';
        System.arraycopy(getName().getBytes("UTF-8"), 0, buffer, 7 + lengthString.length(), getName().length());

        buffer[bufferSize - 1] = '\n';

        return buffer;
    }

}
