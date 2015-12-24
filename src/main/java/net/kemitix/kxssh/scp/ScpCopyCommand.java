package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

/**
 * Represents the SCP Copy command for uploading or downloading a file over SSH.
 *
 * @author pcampbell
 */
public class ScpCopyCommand extends ScpTransferCommand {

    public ScpCopyCommand() {
    }

    public ScpCopyCommand(String commandLine) throws UnsupportedEncodingException {
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
