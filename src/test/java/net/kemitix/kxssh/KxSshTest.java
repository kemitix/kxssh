package net.kemitix.kxssh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class KxSshTest {

    /**
     * Test of constructor, of class KxSsh.
     */
    @Test
    public void testConstructor() {
        //given

        //when
        new KxSsh();

        //then
    }

    /**
     * Test of getSftpClient method, of class KxSsh.
     *
     * Password Authentication
     */
    @Test
    public void testGetSftpClientPassword() {
        //given
        String hostname = "hostname";
        String username = "username";
        String password = "password";

        //when
        KxSsh.getSftpClient(hostname, username, password);

        //then
    }

    /**
     * Test of getSftpClient method, of class KxSsh.
     *
     * Private Key Authentication
     */
    @Test
    public void testGetSftpClientPrivateKey() {
        //given
        String hostname = "hostname";
        String username = "username";
        String privateKey = "private key";
        String passPhrase = "pass phrase";

        //when
        KxSsh.getSftpClient(hostname, username, privateKey, passPhrase);

        //then
    }

}
