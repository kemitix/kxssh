package net.kemitix.kxssh.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import net.kemitix.kxssh.SshAuthentication;
import net.kemitix.kxssh.SshException;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Mockito.mock;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchFactoryTest {

    private JSchFactory factory;

    @Before
    public void setUp() {
        factory = new JSchFactory();
    }

    /**
     * Test of build method, of class JSchFactory.
     *
     * No known hosts or authenticate - invalid
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test(expected = SshException.class)
    public void testBuild() throws SshException, JSchException {
        //given

        //when
        factory.build();

        //then
    }

    /**
     * Test of build method, of class JSchFactory.
     *
     * Set Known Hosts but no authenticate - still invalid
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test(expected = SshException.class)
    public void testBuildKnownHosts() throws SshException, JSchException {
        //given
        String knownHosts = "~/.ssh/known_hosts";

        //when
        factory
                .knownHosts(knownHosts)
                .build();

        //then
    }

    /**
     * Test of build method, of class JSchFactory.
     *
     * With authenticate - valid
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testBuildAuthentication() throws SshException, JSchException {
        //given
        SshAuthentication authentication = mock(SshAuthentication.class);

        //when
        JSch jsch = factory
                .authenticate(authentication)
                .build();

        //then
        assertNotNull(jsch);
    }

}
