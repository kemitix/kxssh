package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ScpCommandTest {

    /**
     * Test of parse method, of class ScpCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParse() throws UnsupportedEncodingException {
        //given

        //when
        ScpCommand.parse("X\n");

        //then
    }

}
