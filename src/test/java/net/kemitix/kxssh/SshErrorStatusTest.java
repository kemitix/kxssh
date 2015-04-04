package net.kemitix.kxssh;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class SshErrorStatusTest {

    private SshErrorStatus status;

    /**
     * Test of values method, of class SshErrorStatus.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        //given

        //when
        SshErrorStatus[] values = SshErrorStatus.values();

        //then
        assertThat(values.length, is(12));
    }

    /**
     * Test of valueOf method, of class SshErrorStatus.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        //given

        //when
        SshErrorStatus valueOf = SshErrorStatus.valueOf("ACK_ERROR");

        //then
        assertThat(valueOf, is(SshErrorStatus.ACK_ERROR));
    }

}
