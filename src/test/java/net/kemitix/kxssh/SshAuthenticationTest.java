package net.kemitix.kxssh;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshAuthenticationTest {

    private SshAuthentication authentication;

    private String username;

    @Before
    public void setUp() {
        username = "username";
        authentication = new SshAuthentication(username);
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
