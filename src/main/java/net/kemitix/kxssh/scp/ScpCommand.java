package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

public abstract class ScpCommand {

    public abstract byte[] getBytes() throws UnsupportedEncodingException;

    public static ScpCommand parse(String commandLine) throws UnsupportedEncodingException {
        char commandPrefix = commandLine.charAt(0);
        switch (commandPrefix) {
            case 'C':
                return new ScpCopyCommand(commandLine);
            case 'D':
                return new ScpDirectoryCommand(commandLine);
            case 'E':
                return new ScpEndCommand();
            case 'T':
                return new ScpTimeCommand(commandLine);
            default:
                throw new IllegalArgumentException("Unknown command: " + commandLine);
        }
    }

}
