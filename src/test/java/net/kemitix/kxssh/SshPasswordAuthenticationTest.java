package net.kemitix.kxssh;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshPasswordAuthenticationTest {

    private SshPasswordAuthentication authentication;
    private String username;
    private String password;

    @Before
    public void setUp() {
        username = "username";
        password = "password";
        authentication = new SshPasswordAuthentication(username, password);
    }

    /**
     * Test of getPassword method, of class SshPasswordAuthentication.
     */
    @Test
    public void testGetPassword() {
        System.out.println("getPassword");
        //given

        //when
        String result = authentication.getPassword();

        //then
        assertThat(result, is(password));
    }

    /**
     * Test of getUsername method, of class SshAuthentication.
     */
    @Test
    public void testGetUsername() {
        System.out.println("getUsername");
        //given

        //when
        String result = authentication.getUsername();

        //then
        assertThat(result, is(username));
    }

}
