package net.kemitix.kxssh;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshIOFactoryTest {

    private SshIOFactory factory;

    @Before
    public void setUp() {
        factory = new SshIOFactory();
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
        factory.createFileOutputStream(new File("temp"));

        //then
    }

}
