package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;

/**
 * Represents the SCP End command for terminating an SCP session.
 *
 * @author pcampbell
 */
public class ScpEndCommand extends ScpCommand {

    @Override
    public byte[] getBytes() throws UnsupportedEncodingException {
        byte[] buffer = new byte[2];
        buffer[0] = 'E';
        buffer[1] = TERMINATOR;
        return buffer;
    }

}
