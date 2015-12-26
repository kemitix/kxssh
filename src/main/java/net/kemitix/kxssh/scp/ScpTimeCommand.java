package net.kemitix.kxssh.scp;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    ScpTimeCommand() {
    }

    /**
     * Constructor.
     *
     * @param commandLine the command to parse
     *
     * @throws UnsupportedEncodingException not thrown
     */
    ScpTimeCommand(final String commandLine)
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
    private void parseCommandLine(final String commandLine)
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
        StringBuilder message = new StringBuilder();
        message.append("T").append(Long.toString(mtime)).append(" 0 ")
                .append(Long.toString(atime)).append(" 0").append(TERMINATOR);
        return message.toString().getBytes("UTF-8");
    }

}
