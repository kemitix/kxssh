package net.kemitix.kxssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshPrivateKeyAuthenticationTest {

    private SshPrivateKeyAuthentication authentication;
    private String username;
    private Path privateKey;
    private String passPhrase;
    private JSch jsch;
    private Session session;

    @Before
    public void setUp() throws IOException {
        username = "username";
        privateKey = Files.createTempFile("private", "key");
        passPhrase = "pass phrase";
        authentication = new SshPrivateKeyAuthentication(username,
                privateKey.toString(), passPhrase);
        jsch = mock(JSch.class);
    }

    @After
    public void tearDown() throws IOException {
        if (Files.exists(privateKey)) {
            Files.delete(privateKey);
        }
    }

    /**
     * Test of prepare method, of class SshPrivateKeyAuthentication.
     *
     * @throws com.jcraft.jsch.JSchException
     */
    @Test
    public void testPrepare() throws JSchException {
        //given

        //when
        authentication.prepare(jsch);

        //then
        verify(jsch, times(1)).addIdentity(privateKey.toString(), passPhrase);
    }

    /**
     * Test of prepare method, of class SshPrivateKeyAuthentication.
     *
     * JSchException is thrown
     *
     * @throws com.jcraft.jsch.JSchException
     */
    @Test(expected = SshException.class)
    public void testPrepareJSchException() throws JSchException {
        //given
        Mockito.doThrow(JSchException.class)
                .when(jsch)
                .addIdentity(privateKey.toString(), passPhrase);

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
        assertThat(result, is(privateKey.toString()));
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
