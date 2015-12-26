package net.kemitix.kxssh.scp;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.arraycopy;

/**
 * Represent the SCP Time command for getting the modify and access times of a
 * file.
 *
 * @author pcampbell
 */
@Setter
@Getter
class ScpTimeCommand extends ScpCommand {

    private long mtime;
    private long atime;

    /**
     * Default constructor.
     */
    public ScpTimeCommand() {
    }

    /**
     * Constructor.
     *
     * @param commandLine the command to parse
     *
     * @throws UnsupportedEncodingException not thrown
     */
    public ScpTimeCommand(String commandLine)
            throws UnsupportedEncodingException {
        parseCommandLine(commandLine);
    }

    /**
     * Parse a command line for the last modified and last accessed times.
     *
     * @param commandLine the command to parse
     *
     * @throws UnsupportedEncodingException not thrown
     */
    private void parseCommandLine(String commandLine)
            throws UnsupportedEncodingException {
        // parse "mtime 0 atime 0"
        Matcher matcher
                = Pattern
                .compile(getCommandPattern())
                .matcher(commandLine);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Illegal command format: " + commandLine);
        }
        mtime = Long.parseLong(matcher.group("mtime"));
        atime = Long.parseLong(matcher.group("atime"));
    }

    /**
     * Returns the regular expression defining the time command.
     *
     * @return the time command as a regular expression
     */
    private static String getCommandPattern() {
        return "^"
                + "T"
                + "(?<mtime>\\d+)\\s"
                + "0\\s"
                + "(?<atime>\\d+)\\s"
                + "0"
                + TERMINATOR
                + "$";
    }

    @Override
    public byte[] getBytes() throws UnsupportedEncodingException {
        String mTimeString = Long.toString(mtime);
        String aTimeString = Long.toString(atime);

        int bufferSize = 7 // command(1), delimiters(3), zeros(2), terminator(1)
                + mTimeString.length()
                + aTimeString.length();

        byte[] buffer = new byte[bufferSize];

        buffer[0] = 'T';

        arraycopy(mTimeString.getBytes("UTF-8"), 0, buffer, 1,
                mTimeString.length());
        buffer[mTimeString.length() + 1] = ' ';
        buffer[mTimeString.length() + 2] = '0';
        buffer[mTimeString.length() + 3] = ' ';
        arraycopy(aTimeString.getBytes("UTF-8"), 0, buffer,
                mTimeString.length() + 4, aTimeString.length());
        buffer[mTimeString.length() + aTimeString.length() + 4] = ' ';
        buffer[mTimeString.length() + aTimeString.length() + 5] = '0';

        buffer[bufferSize - 1] = TERMINATOR;

        return buffer;
    }

}
