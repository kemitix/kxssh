package net.kemitix.kxssh;

/**
 * Factory class for creating instances of {@link IOChannelReadReply}.
 *
 * @author pcampbell
 */
public class IOChannelReadReplyFactory {

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
