package net.kemitix.kxssh.scp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;

@RunWith(BlockJUnit4ClassRunner.class)
public class ScpCommandFactoryTest {

    /**
     * Test of parse method, of class ScpCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParse() throws UnsupportedEncodingException {
        //given

        //when
        ScpCommandFactory.parse("X\n");

        //then
    }

}
