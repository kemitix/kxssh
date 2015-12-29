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

}
