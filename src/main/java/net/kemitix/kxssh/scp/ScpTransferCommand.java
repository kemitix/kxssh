package net.kemitix.kxssh.scp;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class representing an SCP file transfer command.
 *
 * @author pcampbell
 */
@Setter
@Getter
public abstract class ScpTransferCommand extends ScpCommand {

    private final byte[] fileMode = new byte[4];
    private long length;
    private String name;

    /**
     * Default constructor.
     */
    public ScpTransferCommand() {
    }

    /**
     * Constructor.
     *
     * @param commandLine the command to parse
     *
     * @throws UnsupportedEncodingException if there is an error decoding the
     *                                      command
     */
    public ScpTransferCommand(String commandLine) throws UnsupportedEncodingException {
        parseCommandLine(commandLine);
    }

    /**
     * Sets the file's Unix file permissions mode.
     *
     * @param fileMode the file mode
     */
    public void setFileMode(byte[] fileMode) {
        if (fileMode.length != 4) {
            throw new IllegalArgumentException("File mode must be 4-byte array");
        }
        System.arraycopy(fileMode, 0, this.fileMode, 0, 4);
    }

    /**
     * Returns the regular expression the match the command against.
     *
     * @return the regular expression
     */
    protected abstract String getCommandPattern();

    /**
     * Parses the command for the transfer length and name.
     *
     * @param commandLine the command to parse.
     *
     * @throws UnsupportedEncodingException if there is an error decoding the
     *                                      command
     */
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

        buffer[bufferSize - 1] = TERMINATOR;

        return buffer;
    }

}
