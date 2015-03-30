package net.kemitix.kxssh.jsch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class IOChannelMetadataTest {

    private IOChannelMetadata ioChannelMetadata;
    private byte[] header;
    private int filesize;
    private String filename;

    @Before
    public void setUp() {
        ioChannelMetadata = new IOChannelMetadata();
        header = new byte[]{'h', 'e', 'a', 'd', 'e', 'r'};
        filesize = 987645;
        filename = "filename.txt";
    }

    /**
     * Test of setHeader, getHeader methods, of class IOChannelMetadata.
     */
    @Test
    public void testSetGetHeader() {
        System.out.println("set/getHeader");
        //given

        //when
        ioChannelMetadata.setHeader(header);
        byte[] result = ioChannelMetadata.getHeader();

        //then
        assertThat(result, is(header));
    }

    /**
     * Test of setFilesize, getFilesize methods, of class IOChannelMetadata.
     */
    @Test
    public void testSetGetFilesize() {
        System.out.println("set/getFilesize");
        //given

        //when
        ioChannelMetadata.setFilesize(filesize);
        int result = ioChannelMetadata.getFilesize();

        //then
        assertThat(result, is(filesize));
    }

    /**
     * Test of setFilename, getFilename methods, of class IOChannelMetadata.
     */
    @Test
    public void testSetGetFilename() {
        System.out.println("set/getFilename");
        //given

        //when
        ioChannelMetadata.setFilename(filename);
        String result = ioChannelMetadata.getFilename();

        //then
        assertThat(result, is(filename));
    }

}
