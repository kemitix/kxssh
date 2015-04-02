package net.kemitix.kxssh;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshOperationStatusTest {

    private SshOperationStatus status;

    /**
     * Test of values method, of class SshOperationStatus.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        //given

        //when
        SshOperationStatus[] values = SshOperationStatus.values();

        //then
        assertThat(values.length, is(7));
    }

    /**
     * Test of valueOf method, of class SshOperationStatus.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        //given

        //when
        SshOperationStatus valueOf = SshOperationStatus.valueOf("CONNECTED");

        //then
        assertThat(valueOf, is(SshOperationStatus.CONNECTED));
    }

}
