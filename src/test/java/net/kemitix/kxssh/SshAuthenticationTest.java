package net.kemitix.kxssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Mockito.mock;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshAuthenticationTest {

    private SshAuthentication authentication;
    private Session session;
    private String username;

    @Before
    public void setUp() {
        username = "username";
        session = mock(Session.class);
        authentication = new SshAuthentication(username) {

            @Override
            public void prepare(JSch jsch) {
                jsch.getClass();
            }

            @Override
            public void authenticateSession(Session session) {
                session.getClass();
            }
        };
    }

    /**
     * Test of getUsername method, of class SshAuthentication.
     */
    @Test
    public void testGetUsername() {
        //given

        //when
        String result = authentication.getUsername();

        //then
        assertThat(result, is(username));
    }

}
