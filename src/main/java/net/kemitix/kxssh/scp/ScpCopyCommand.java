package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

/**
 * Represents the SCP Copy command for uploading or downloading a file over SSH.
 *
 * @author pcampbell
 */
public class ScpCopyCommand extends ScpTransferCommand {

    /**
     * Default constructor.
     */
    public ScpCopyCommand() {
    }

    /**
     * Constructor.
     *
     * @param commandLine the command to parse
     *
     * @throws UnsupportedEncodingException if there is an error decoding the
     *                                      command
     */
    public ScpCopyCommand(final String commandLine)
            throws UnsupportedEncodingException {
        super(commandLine);
    }

    @Override
    protected String getCommandPattern() {
        return "^"
                + "C"
                + "(?<mode>\\d\\d\\d\\d)\\s"
                + "(?<length>\\d+)\\s"
                + "(?<name>.+)"
                + TERMINATOR + "$";
    }

}
