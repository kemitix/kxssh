package net.kemitix.kxssh.jsch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class IOChannelReadReplyFactoryTest {

    private IOChannelReadReplyFactory factory;

    @Before
    public void setUp() {
        factory = new IOChannelReadReplyFactory();
    }

    /**
     * Test of createReply method, of class IOChannelReadReplyFactory.
     */
    @Test
    public void testCreateReply() {
        System.out.println("createReply");
        //given
        int bytesRequested = 5;
        int bytesRead = 4;
        byte[] buffer = new byte[bytesRequested];

        //when
        IOChannelReadReply reply = factory.createReply(bytesRequested, bytesRead, buffer);

        //then
        assertThat(reply.getBytesRequested(), is(bytesRequested));
        assertThat(reply.getBytesRead(), is(bytesRead));
        assertThat(reply.getBuffer(), is(buffer));
    }

}
