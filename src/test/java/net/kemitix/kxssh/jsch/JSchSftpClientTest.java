package net.kemitix.kxssh.jsch;

import java.io.File;
import net.kemitix.kxssh.SshConnectionProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(BlockJUnit4ClassRunner.class)
public class JSchSftpClientTest {

    private JSchSftpClient client;
    private SshConnectionProperties connectionProperties;
    private JSchDownload download;

    @Before
    public void setUp() {
        connectionProperties = mock(SshConnectionProperties.class);
        download = mock(JSchDownload.class);
        client = new JSchSftpClient(connectionProperties);
        client.setDownload(download);
    }

    /**
     * Test of download method, of class JSchSftpClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDownload() throws Exception {
        System.out.println("download");
        //given
        String remote = "remote.txt";
        File local = new File("local.txt");

        //when
        client.download(remote, local);

        //then
        verify(download, times(1)).download(remote, local);
    }

}
