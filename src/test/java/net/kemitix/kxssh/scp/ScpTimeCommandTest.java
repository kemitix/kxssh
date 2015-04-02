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
public class ScpTimeCommandTest {

    private ScpTimeCommand scpTimeCommand;
    private long mTime;
    private long aTime;

    @Before
    public void setUp() {
        scpTimeCommand = new ScpTimeCommand();
        mTime = 1183828267;
        aTime = 1183008267;
    }

    /**
     * Test of get/setMTime methods, of class ScpCopyCommand.
     */
    @Test
    public void testGetSetMTime() {
        System.out.println("get/setMTime");
        //given

        //when
        scpTimeCommand.setMtime(mTime);

        //then
        assertThat(scpTimeCommand.getMtime(), is(mTime));
    }

    /**
     * Test of get/setATime methods, of class ScpCopyCommand.
     */
    @Test
    public void testGetSetATime() {
        System.out.println("get/setATime");
        //given

        //when
        scpTimeCommand.setAtime(aTime);

        //then
        assertThat(scpTimeCommand.getAtime(), is(aTime));
    }

    /**
     * Test of getBytes method, of class ScpCopyCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testGetBytes() throws UnsupportedEncodingException {
        System.out.println("getBytes");
        //given
        scpTimeCommand.setMtime(mTime);
        scpTimeCommand.setAtime(aTime);

        String command = "T1183828267 0 1183008267 0\n";
        byte[] expected = command.getBytes("UTF-8");

        //when
        byte[] result = scpTimeCommand.getBytes();

        //then
        assertThat(result, is(expected));
    }

    /**
     * Test of parse method, of class ScpCopyCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testParseCommand() throws UnsupportedEncodingException {
        System.out.println("parseCommand");
        //given
        String command = "T1183828267 0 1183008267 0\n";

        //when
        ScpCommand result = ScpCommand.parse(command);

        //then
        assertThat(result, is(instanceOf(ScpTimeCommand.class)));
        assertThat(((ScpTimeCommand) result).getMtime(), is(mTime));
        assertThat(((ScpTimeCommand) result).getAtime(), is(aTime));
    }

    /**
     * Test of parse method, of class ScpCopyCommand.
     *
     * With bad command format
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseCommandBadFormat() throws UnsupportedEncodingException {
        System.out.println("parseCommand bad format");
        //given
        String command = "T1183828267  1183008267 0\n";

        //when
        ScpCommand.parse(command);

        //then
    }

}
