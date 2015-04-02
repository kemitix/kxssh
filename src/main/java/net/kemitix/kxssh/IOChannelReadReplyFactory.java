package net.kemitix.kxssh;

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
