package net.kemitix.kxssh.scp;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Abstract class representing an SCP file transfer command.
 *
 * @author pcampbell
 */
@Setter
@Getter
@SuppressWarnings("unused")
public abstract class ScpTransferCommand extends ScpCommand {

    /**
     * The number of bytes in the Unix file mode permissions array.
     */
    private static final int FILE_MODE_LENGTH = 4;

    private final byte[] fileMode = new byte[FILE_MODE_LENGTH];
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
    public ScpTransferCommand(final String commandLine)
            throws UnsupportedEncodingException {
        parseCommandLine(commandLine);
    }

    /**
     * Sets the file's Unix file permissions mode.
     *
     * @param unixPermissions the file mode
     */
    public void setFileMode(final String unixPermissions) {
        if (unixPermissions.length() != FILE_MODE_LENGTH) {
            throw new IllegalArgumentException(
                    "File mode must be 4-byte array");
        }
        arraycopy(unixPermissions.getBytes(UTF_8), 0, fileMode, 0,
                FILE_MODE_LENGTH);
    }

    /**
     * Returns the Unix file mode permissions.
     *
     * @return the Unix file mode permissions
     */
    public String getFileMode() {
        return new String(fileMode, UTF_8);
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
    private void parseCommandLine(final String commandLine)
            throws UnsupportedEncodingException {
        // parse "mmmm length filename"
        Matcher matcher
                = Pattern
                .compile(getCommandPattern())
                .matcher(commandLine);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Illegal command format: " + commandLine);
        }
        arraycopy(matcher.group("mode").getBytes("UTF-8"), 0, fileMode, 0,
                FILE_MODE_LENGTH);
        length = Long.parseLong(matcher.group("length"));
        name = matcher.group("name");
    }

    @Override
    public byte[] getBytes() throws UnsupportedEncodingException {
        String lengthString = Long.toString(getLength());

        // command(1), fileMode(4), delimiters(2), terminator(1)
        int bufferSize = 1 + FILE_MODE_LENGTH + 2 + 1
                + lengthString.length()
                + getName().length();

        byte[] buffer = new byte[bufferSize];

        if (this instanceof ScpCopyCommand) {
            buffer[0] = 'C';
        } else {
            buffer[0] = 'D';
        }

        arraycopy(fileMode, 0, buffer, 1, FILE_MODE_LENGTH);
        buffer[FILE_MODE_LENGTH + 1] = ' ';
        arraycopy(lengthString.getBytes("UTF-8"), 0, buffer,
                FILE_MODE_LENGTH + 2, lengthString.length());
        buffer[FILE_MODE_LENGTH + 2 + lengthString.length()] = ' ';
        arraycopy(getName().getBytes("UTF-8"), 0, buffer,
                1 + FILE_MODE_LENGTH + 2 + lengthString.length(),
                getName().length());

        buffer[bufferSize - 1] = TERMINATOR;

        return buffer;
    }

}
