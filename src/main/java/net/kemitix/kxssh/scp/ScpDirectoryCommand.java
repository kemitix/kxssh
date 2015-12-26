package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

/**
 * Represents the SCP Directory command for a recursive directory copy.
 *
 * @author pcampbell
 */
class ScpDirectoryCommand extends ScpTransferCommand {

    /**
     * Default constructor.
     */
    public ScpDirectoryCommand() {
        setLength(0);
    }

    /**
     * Constructor.
     *
     * @param commandLine the command to parse
     *
     * @throws UnsupportedEncodingException not thrown
     */
    public ScpDirectoryCommand(String commandLine)
            throws UnsupportedEncodingException {
        super(commandLine);
        setLength(0);
    }

    @Override
    protected String getCommandPattern() {
        return "^"
                + "D"
                + "(?<mode>\\d\\d\\d\\d)\\s"
                + "(?<length>\\d)\\s"
                + "(?<name>.+)"
                + TERMINATOR
                + "$";
    }

}
