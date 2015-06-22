package net.kemitix.kxssh;

import com.jcraft.jsch.Session;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshPasswordAuthenticationTest {

    private SshPasswordAuthentication authentication;
    private String username;
    private String password;
    private Session session;

    @Before
    public void setUp() {
        username = "username";
        password = "password";
        authentication = new SshPasswordAuthentication(username, password);
        session = mock(Session.class);
    }

    /**
     * Test of getPassword method, of class SshPasswordAuthentication.
     */
    @Test
    public void testGetPassword() {
        //given

        //when
        String result = authentication.getPassword();

        //then
        assertThat(result, is(password));
    }

    /**
     * Test of getUsername method, of class SshPasswordAuthentication.
     */
    @Test
    public void testGetUsername() {
        //given

        //when
        String result = authentication.getUsername();

        //then
        assertThat(result, is(username));
    }

    /**
     * Test of authenticateSession method, of class SshPasswordAuthentication.
     */
    @Test
    public void testAuthenticateSession() {
        //given

        //when
        authentication.authenticateSession(session);

        //then
        verify(session, times(1)).setPassword(password);
    }

}
