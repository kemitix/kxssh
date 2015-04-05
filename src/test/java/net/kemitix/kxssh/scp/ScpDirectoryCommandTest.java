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
public class ScpDirectoryCommandTest {

    private ScpDirectoryCommand scpDirectoryCommand;
    private byte[] fileMode;
    private String name;

    @Before
    public void setUp() {
        scpDirectoryCommand = new ScpDirectoryCommand();
        fileMode = new byte[4];
        fileMode[0] = '0';
        fileMode[1] = '7';
        fileMode[2] = '6';
        fileMode[3] = '4';
        name = "directory name";
    }

    /**
     * Test of ScpDirectoryCommand(String) method, of class ScpDirectoryCommand.
     *
     * @throws java.io.UnsupportedEncodingException
     */
    @Test
    public void testConstructorString() throws UnsupportedEncodingException {
        System.out.println("new ScpDirectoryCommand(String)");
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
        System.out.println("get/setFileMode");
        //given

        //when
        scpDirectoryCommand.setFileMode(fileMode);

        //then
        assertThat(scpDirectoryCommand.getFileMode()[0], is(fileMode[0]));
        assertThat(scpDirectoryCommand.getFileMode()[1], is(fileMode[1]));
        assertThat(scpDirectoryCommand.getFileMode()[2], is(fileMode[2]));
        assertThat(scpDirectoryCommand.getFileMode()[3], is(fileMode[3]));
    }

    /**
     * Test of get/setName methods, of class ScpDirectoryCommand.
     */
    @Test
    public void testGetSetName() {
        System.out.println("get/setName");
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
        System.out.println("getBytes");
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
        System.out.println("getBytes with bad file mode array length");
        //given
        byte[] shortFileMode = new byte[3];
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
        System.out.println("parseCommand");
        //given
        String command = "D0764 0 directory name" + ScpCommand.TERMINATOR;

        //when
        ScpCommand result = ScpCommand.parse(command);

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
        System.out.println("parseCommand bad format");
        //given
        String command = "D0764 1234 directory name" + ScpCommand.TERMINATOR;

        //when
        ScpCommand.parse(command);

        //then
    }

}
