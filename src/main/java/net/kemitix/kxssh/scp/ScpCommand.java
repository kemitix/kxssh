package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

/**
 * Abstract class representing an SCP command.
 *
 * @author pcampbell
 */
public abstract class ScpCommand {

    public static final char TERMINATOR = '\n';

    /**
     * Return the command as a byte array ready for transmission.
     *
     * @return the command as a byte array
     *
     * @throws UnsupportedEncodingException if there is an error encoding the
     *                                      array
     */
    public abstract byte[] getBytes() throws UnsupportedEncodingException;

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
