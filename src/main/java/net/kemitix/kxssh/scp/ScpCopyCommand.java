package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

public class ScpCopyCommand extends ScpCDCommand {

    public ScpCopyCommand() {
    }

    public ScpCopyCommand(String commandLine) throws UnsupportedEncodingException {
        super(commandLine);
    }

    @Override
    protected String getCommandPattern() {
        return "^[CD](?<mode>\\d\\d\\d\\d)\\s(?<length>\\d+)\\s(?<name>.+)\n$";
    }

}
