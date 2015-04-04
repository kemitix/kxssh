package net.kemitix.kxssh;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IOChannelReadReply {

    private int bytesRequested;
    private int bytesRead;
    private byte[] buffer;

}
