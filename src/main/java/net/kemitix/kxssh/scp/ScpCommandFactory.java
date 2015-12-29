package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

/**
 * Factory for creating {@link ScpCommand} implementations.
 *
 * @author pcampbell
 */
public interface ScpCommandFactory {

    /**
     * Parse the command line into the appropriate command object.
     *
     * @param commandLine the command the be parsed
     *
     * @return the command object
     *
     * @throws UnsupportedEncodingException if there is an error decoding the
     *                                      command
     */
    static ScpCommand parse(final String commandLine)
            throws UnsupportedEncodingException {
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
                throw new IllegalArgumentException(
                        "Unknown command: " + commandLine);
        }
    }

}
