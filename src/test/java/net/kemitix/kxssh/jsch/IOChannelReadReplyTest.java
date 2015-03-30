package net.kemitix.kxssh.jsch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class IOChannelReadReplyTest {

    private IOChannelReadReply reply;

    @Before
    public void setUp() {
        reply = new IOChannelReadReply();
    }

    /**
     * Test of set/getBytesRequested method, of class IOChannelReadReply.
     */
    @Test
    public void testSetGetBytesRequested() {
        System.out.println("set/getBytesRequested");
        //given
        int bytes = 1000;

        //when
        reply.setBytesRequested(bytes);

        //then
        assertThat(reply.getBytesRequested(), is(bytes));
    }

    /**
     * Test of set/getBytesRead method, of class IOChannelReadReply.
     */
    @Test
    public void testSetGetBytesRead() {
        System.out.println("set/getBytesRead");
        //given
        int bytes = 1000;

        //when
        reply.setBytesRead(bytes);

        //then
        assertThat(reply.getBytesRead(), is(bytes));
    }

    /**
     * Test of set/getBuffer method, of class IOChannelReadReply.
     */
    @Test
    public void testSetGetBuffer() {
        System.out.println("set/getBuffer");
        //given
        byte[] buffer = new byte[1000];

        //when
        reply.setBuffer(buffer);

        //then
        assertThat(reply.getBuffer(), is(buffer));
    }

}
