package net.kemitix.kxssh.scp;

import java.io.UnsupportedEncodingException;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ScpEndCommandTest {

    private ScpEndCommand scpEndCommand;

    @Before
    public void setUp() {
        scpEndCommand = new ScpEndCommand();
    }

    /**
     * Test of getBytes method, of class ScpEndCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testGetBytes() throws UnsupportedEncodingException {
        System.out.println("getBytes");
        //given

        String command = "E\n";
        byte[] expected = command.getBytes("UTF-8");

        //when
        byte[] result = scpEndCommand.getBytes();

        //then
        assertThat(result, is(expected));
    }

    /**
     * Test of parse method, of class ScpEndCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testParseCommand() throws UnsupportedEncodingException {
        System.out.println("parseCommand");
        //given
        String command = "E\n";

        //when
        ScpCommand result = ScpCommand.parse(command);

        //then
        assertThat(result, is(instanceOf(ScpEndCommand.class)));
    }

}