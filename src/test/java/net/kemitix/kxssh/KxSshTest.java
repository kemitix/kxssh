package net.kemitix.kxssh;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RunWith(BlockJUnit4ClassRunner.class)
public class KxSshTest {

    private Path privateKey;

    /**
     * Prepare each test.
     *
     * @throws IOException if error creating temp file
     */
    @Before
    public void setUp() throws IOException {
        privateKey = Files.createTempFile("private", "key");
    }

    /**
     * Clean up after each test.
     *
     * @throws IOException if error deleting temp file
     */
    @After
    public void tearDown() throws IOException {
        if (Files.exists(privateKey)) {
            Files.delete(privateKey);
        }
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
        String passPhrase = "pass phrase";

        //when
        KxSsh.getSftpClient(hostname, username, privateKey.toString(),
                passPhrase);

        //then
    }

}
