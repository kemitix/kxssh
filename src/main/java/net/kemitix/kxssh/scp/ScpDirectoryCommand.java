package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

class ScpDirectoryCommand extends ScpCDCommand {

    public ScpDirectoryCommand() {
        setLength(0);
    }

    public ScpDirectoryCommand(String commandLine) throws UnsupportedEncodingException {
        super(commandLine);
        setLength(0);
    }

    @Override
    protected String getCommandPattern() {
        return "^[CD](?<mode>\\d\\d\\d\\d)\\s(?<length>\\d)\\s(?<name>.+)\n$";
    }

}
