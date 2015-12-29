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
public class ScpDirectoryCommandTest {

    private ScpDirectoryCommand scpDirectoryCommand;
    private String fileMode;
    private String name;

    @Before
    public void setUp() {
        scpDirectoryCommand = new ScpDirectoryCommand();
        fileMode = "0764";
        name = "directory name";
    }

    /**
     * Test of ScpDirectoryCommand(String) method, of class ScpDirectoryCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testConstructorString() throws UnsupportedEncodingException {
        //given
        String commandLine = "D0764 0 directory name" + ScpCommand.TERMINATOR;

        //when
        ScpDirectoryCommand command = new ScpDirectoryCommand(commandLine);

        //then
        assertThat(command.getFileMode(), is(fileMode));
        assertThat(command.getName(), is("directory name"));
    }

    /**
     * Test of get/setFileMode methods, of class ScpDirectoryCommand.
     */
    @Test
    public void testGetSetFileMode() {
        //given

        //when
        scpDirectoryCommand.setFileMode(fileMode);

        //then
        assertEquals(fileMode, scpDirectoryCommand.getFileMode());
    }

    /**
     * Test of get/setName methods, of class ScpDirectoryCommand.
     */
    @Test
    public void testGetSetName() {
        //given

        //when
        scpDirectoryCommand.setName(name);

        //then
        assertThat(scpDirectoryCommand.getName(), is(name));
    }

    /**
     * Test of getBytes method, of class ScpDirectoryCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testGetBytes() throws UnsupportedEncodingException {
        //given
        scpDirectoryCommand.setFileMode(fileMode);
        scpDirectoryCommand.setName(name);

        String command = "D0764 0 directory name" + ScpCommand.TERMINATOR;
        byte[] expected = command.getBytes("UTF-8");

        //when
        byte[] result = scpDirectoryCommand.getBytes();

        //then
        assertThat(result, is(expected));
    }

    /**
     * Test of getBytes method, of class ScpDirectoryCommand.
     *
     * With incorrect file mode array length
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetBytesBadFileModeArray() throws UnsupportedEncodingException {
        //given
        String shortFileMode = "123";
        scpDirectoryCommand.setFileMode(shortFileMode);
        scpDirectoryCommand.setName(name);

        //when
        scpDirectoryCommand.getBytes();
    }

    /**
     * Test of parse method, of class ScpDirectoryCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testParseCommand() throws UnsupportedEncodingException {
        //given
        String command = "D0764 0 directory name" + ScpCommand.TERMINATOR;

        //when
        ScpCommand result = ScpCommandFactory.parse(command);

        //then
        assertThat(result, is(instanceOf(ScpDirectoryCommand.class)));
        assertThat(((ScpDirectoryCommand) result).getFileMode(), is(fileMode));
        assertThat(((ScpDirectoryCommand) result).getName(), is(name));
    }

    /**
     * Test of parse method, of class ScpDirectoryCommand.
     *
     * With bad command format
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseCommandBadFormat() throws UnsupportedEncodingException {
        //given
        String command = "D0764 1234 directory name" + ScpCommand.TERMINATOR;

        //when
        ScpCommandFactory.parse(command);

        //then
    }

}
