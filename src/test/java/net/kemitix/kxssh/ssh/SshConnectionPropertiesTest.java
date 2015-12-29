package net.kemitix.kxssh.ssh;

import net.kemitix.kxssh.ssh.SshPasswordAuthentication;
import net.kemitix.kxssh.ssh.SshConnectionProperties;
import net.kemitix.kxssh.ssh.SshAuthentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshConnectionPropertiesTest {

    private SshConnectionProperties connectionProperties;
    private String hostname;
    private SshAuthentication authentication;
    private String username;
    private String password;

    @Before
    public void setUp() {
        hostname = "hostname";
        username = "username";
        password = "password";
        authentication = new SshPasswordAuthentication(username, password);
        connectionProperties = new SshConnectionProperties(hostname, authentication);
    }

    /**
     * Test of getHostname method, of class SshConnectionProperties.
     */
    @Test
    public void testGetHostname() {
        //given

        //when
        String result = connectionProperties.getHostname();

        //then
        assertThat(result, is(hostname));
    }

    /**
     * Test of getAuthentication method, of class SshConnectionProperties.
     */
    @Test
    public void testGetAuthentication() {
        //given

        //when
        SshAuthentication result = connectionProperties.getAuthentication();

        //then
        assertThat(result, is(authentication));
        assertThat(result.getUsername(), is(username));
        assertThat(((SshPasswordAuthentication) result).getPassword(), is(password));
    }

}
