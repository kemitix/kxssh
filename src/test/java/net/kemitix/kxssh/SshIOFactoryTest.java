package net.kemitix.kxssh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshIOFactoryTest {

    private SshIOFactory factory;
    private File tempFile;

    @Before
    public void setUp() throws IOException {
        factory = new SshIOFactory();
        tempFile = File.createTempFile("sshIOFactoryTest", ".tmp");
    }

    /**
     * Test of createFileOutputStream method, of class SshIOFactory.
     *
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testCreateFileOutputStream() throws FileNotFoundException {
        System.out.println("createFileOutputStream");
        //given

        //when
        factory.createFileOutputStream(tempFile);

        //then
    }

    @After
    public void tearDown() {
        tempFile.delete();
    }

}
