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
public class ScpCopyCommandTest {

    private ScpCopyCommand scpCopyCommand;
    private byte[] fileMode;
    private long length;
    private String name;

    @Before
    public void setUp() {
        scpCopyCommand = new ScpCopyCommand();
        fileMode = new byte[4];
        fileMode[0] = '0';
        fileMode[1] = '7';
        fileMode[2] = '6';
        fileMode[3] = '4';
        length = 1234L;
        name = "file name";
    }

    /**
     * Test of get/setFileMode methods, of class ScpCopyCommand.
     */
    @Test
    public void testGetSetFileMode() {
        System.out.println("get/setFileMode");
        //given

        //when
        scpCopyCommand.setFileMode(fileMode);

        //then
        assertThat(scpCopyCommand.getFileMode()[0], is(fileMode[0]));
        assertThat(scpCopyCommand.getFileMode()[1], is(fileMode[1]));
        assertThat(scpCopyCommand.getFileMode()[2], is(fileMode[2]));
        assertThat(scpCopyCommand.getFileMode()[3], is(fileMode[3]));
    }

    /**
     * Test of get/setLength methods, of class ScpCopyCommand.
     */
    @Test
    public void testGetSetLength() {
        System.out.println("get/setLength");
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
        System.out.println("get/setName");
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
        System.out.println("getBytes");
        //given
        scpCopyCommand.setFileMode(fileMode);
        scpCopyCommand.setLength(length);
        scpCopyCommand.setName(name);

        String command = "C0764 1234 file name\r";
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
        System.out.println("getBytes with bad file mode array length");
        //given
        byte[] shortFileMode = new byte[3];
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
        System.out.println("parseCommand");
        //given
        String command = "C0764 1234 file name\r";

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
        System.out.println("parseCommand bad format");
        //given
        String command = "C0764 1234file name\r";

        //when
        ScpCommand.parse(command);

        //then
    }

}
