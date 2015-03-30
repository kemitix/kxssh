package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchFactoryTest {

    private JSchFactory factory;

    @Before
    public void setUp() {
        factory = new JSchFactory();
    }

    /**
     * Test of build method, of class JSchFactory.
     */
    @Test
    public void testBuild() {
        System.out.println("build");
        //given
        //when
        JSch jsch = factory.build();

        //then
        assertNotNull(jsch);
    }

    /**
     * Test of build method, of class JSchFactory.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBuildKnownHosts() throws Exception {
        System.out.println("build(string)");
        //given
        String knownHosts = "~/.ssh/known_hosts";

        //when
        JSch jsch = factory.build(knownHosts);

        //then
        assertNotNull(jsch);
    }

}
