package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.NotRegPDU;

import static org.junit.Assert.*;

/**
 * Created by marccoquand on 29/09/15.
 */
public class NotRegPDUTest {
    private final NotRegPDU pdu = new NotRegPDU();

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        assertEquals(OpCode.NOTREG.value, pdu.toByteArray()[0]);
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws Exception {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }
}