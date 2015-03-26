package net.kemitix.kxssh.jsch;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IOChannelMetadata {

    private byte header[];
    private int filesize;
    private String filename;

}
