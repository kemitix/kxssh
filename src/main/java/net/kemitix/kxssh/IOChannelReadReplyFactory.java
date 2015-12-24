package net.kemitix.kxssh;

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
            int bytesRequested,
            int bytesRead,
            byte[] buffer) {
        IOChannelReadReply reply = new IOChannelReadReply();
        reply.setBytesRequested(bytesRequested);
        reply.setBytesRead(bytesRead);
        reply.setBuffer(buffer);
        return reply;
    }

}
