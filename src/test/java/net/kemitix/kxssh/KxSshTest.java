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
        System.out.println("constructor");
        //given

        //when
        new KxSsh();

        //then
    }

    /**
     * Test of getSftpClient method, of class KxSsh.
     */
    @Test
    public void testGetSftpClient() {
        System.out.println("getSftpClient");
        //given
        String hostname = "hostname";
        String username = "username";
        String password = "password";

        //when
        KxSsh.getSftpClient(hostname, username, password);

        //then
    }

}
