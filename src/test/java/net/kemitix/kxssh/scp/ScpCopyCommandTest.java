package net.kemitix.kxssh.scp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class ScpCopyCommandTest {

    private ScpCopyCommand scpCopyCommand;
    private String fileMode;
    private long length;
    private String name;

    @Before
    public void setUp() {
        scpCopyCommand = new ScpCopyCommand();
        fileMode = "0764";
        length = 1234L;
        name = "file name";
    }

    /**
     * Test of get/setFileMode methods, of class ScpCopyCommand.
     */
    @Test
    public void testGetSetFileMode() {
        //given

        //when
        scpCopyCommand.setFileMode(fileMode);

        //then
        assertEquals(fileMode, scpCopyCommand.getFileMode());
    }

    /**
     * Test of get/setLength methods, of class ScpCopyCommand.
     */
    @Test
    public void testGetSetLength() {
        //given

        //when
        scpCopyCommand.setLength(length);

        //then
        assertThat(scpCopyCommand.getLength(), is(length));
    }

    /**
     * Test of get/setName methods, of class ScpCopyCommand.
     */
    @Test
    public void testGetSetName() {
        //given

        //when
        scpCopyCommand.setName(name);

        //then
        assertThat(scpCopyCommand.getName(), is(name));
    }

    /**
     * Test of getBytes method, of class ScpCopyCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testGetBytes() throws UnsupportedEncodingException {
        //given
        scpCopyCommand.setFileMode(fileMode);
        scpCopyCommand.setLength(length);
        scpCopyCommand.setName(name);

        String command = "C0764 1234 file name" + ScpCommand.TERMINATOR;
        byte[] expected = command.getBytes("UTF-8");

        //when
        byte[] result = scpCopyCommand.getBytes();

        //then
        assertThat(result, is(expected));
    }

    /**
     * Test of getBytes method, of class ScpCopyCommand.
     *
     * With incorrect file mode array length
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetBytesBadFileModeArray() throws UnsupportedEncodingException {
        //given
        String shortFileMode = "123";
        scpCopyCommand.setFileMode(shortFileMode);
        scpCopyCommand.setLength(length);
        scpCopyCommand.setName(name);

        //when
        scpCopyCommand.getBytes();
    }

    /**
     * Test of parse method, of class ScpCopyCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testParseCommand() throws UnsupportedEncodingException {
        //given
        String command = "C0764 1234 file name" + ScpCommand.TERMINATOR;

        //when
        ScpCommand result = ScpCommand.parse(command);

        //then
        assertThat(result, is(instanceOf(ScpCopyCommand.class)));
        assertThat(((ScpCopyCommand) result).getFileMode(), is(fileMode));
        assertThat(((ScpCopyCommand) result).getLength(), is(length));
        assertThat(((ScpCopyCommand) result).getName(), is(name));
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
        //given
        String command = "C0764 1234file name" + ScpCommand.TERMINATOR;

        //when
        ScpCommand.parse(command);

        //then
    }

}
