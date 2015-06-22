package net.kemitix.kxssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshPrivateKeyAuthenticationTest {

    private SshPrivateKeyAuthentication authentication;
    private String username;
    private String privateKey;
    private String passPhrase;
    private JSch jsch;
    private Session session;

    @Before
    public void setUp() {
        username = "username";
        privateKey = "private key";
        passPhrase = "pass phrase";
        authentication = new SshPrivateKeyAuthentication(username, privateKey, passPhrase);

        jsch = mock(JSch.class);
    }

    /**
     * Test of prepare method, of class SshPrivateKeyAuthentication.
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testPrepare() throws SshException, JSchException {
        //given

        //when
        authentication.prepare(jsch);

        //then
        verify(jsch, times(1)).addIdentity(privateKey, passPhrase);
    }

    /**
     * Test of prepare method, of class SshPrivateKeyAuthentication.
     *
     * JSchException is thrown
     *
     * @throws net.kemitix.kxssh.SshException
     * @throws com.jcraft.jsch.JSchException
     */
    @Test(expected = SshException.class)
    public void testPrepareJSchException() throws SshException, JSchException {
        //given
        Mockito.doThrow(JSchException.class)
                .when(jsch)
                .addIdentity(privateKey, passPhrase);

        //when
        authentication.prepare(jsch);

        //then
    }

    /**
     * Test of authenticateSession method, of class SshPrivateKeyAuthentication.
     */
    @Test
    public void testAuthenticateSession() {
        //given

        //when
        authentication.authenticateSession(session);

        //then
    }

    /**
     * Test of getPrivateKey method, of class SshPrivateKeyAuthentication.
     */
    @Test
    public void testGetPrivateKey() {
        //given

        //when
        String result = authentication.getPrivateKey();

        //then
        assertThat(result, is(privateKey));
    }

    /**
     * Test of getPassPhrase method, of class SshPrivateKeyAuthentication.
     */
    @Test
    public void testGetPassPhrase() {
        //given

        //when
        String result = authentication.getPassPhrase();

        //then
        assertThat(result, is(passPhrase));
    }

}
