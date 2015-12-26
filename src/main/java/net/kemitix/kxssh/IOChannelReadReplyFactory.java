package net.kemitix.kxssh;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Factory class for creating instances of {@link IOChannelReadReply}.
 *
 * @author pcampbell
 */
public class IOChannelReadReplyFactory {

    /**
     * Creates an {@link IOChannelReadReply}.
     *
     * @param bytesRequested the number of bytes that were to be read
     * @param bytesRead      the number of bytes that were read
     * @param buffer         the buffer containing what was read
     *
     * @return an {@link IOChannelReadReply}
     */
    public IOChannelReadReply createReply(
            final int bytesRequested,
            final int bytesRead,
            final byte[] buffer) {
        IOChannelReadReply reply = new IOChannelReadReply();
        reply.setBytesRequested(bytesRequested);
        reply.setBytesRead(bytesRead);
        reply.setBuffer(new String(buffer, UTF_8));
        return reply;
    }

}
