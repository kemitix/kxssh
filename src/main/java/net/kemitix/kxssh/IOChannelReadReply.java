package net.kemitix.kxssh;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the result of reading from an InputStream.
 *
 * @author pcampbell
 */
@Setter
@Getter
@SuppressWarnings("unused")
public class IOChannelReadReply {

    private int bytesRequested;
    private int bytesRead;
    private String buffer;

}
