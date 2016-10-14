package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.AlivePDU;

import static org.junit.Assert.*;

/**
 * Created by marccoquand on 29/09/15.
 */
public class AlivePDUTest {

    private final byte clientCount = 2;
    private final short id = 12;
    private final AlivePDU pdu = new AlivePDU(clientCount,id);

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        assertEquals(OpCode.ALIVE.value, pdu.toByteArray()[0]);
    }

    @Test
    public void shouldHaveCorrectClientCount() throws Exception {
        assertEquals(clientCount, pdu.toByteArray()[1]);
    }

    @Test
    public void shouldHaveCorrectId() throws Exception {
        byte [] byteArray = pdu.toByteArray();
        short byteId =
                (short) (((((int) byteArray[2]) & 0xff) << 8) +
                        (((int) byteArray[3]) & 0xff));
        assertEquals(id, byteId);
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws Exception {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }
}