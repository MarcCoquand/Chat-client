package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.AckPDU;

import static org.junit.Assert.*;
/**
 * Created by marccoquand on 29/09/15.
 */
public class AckPDUTest {
    private final short ID = 11;
    private final AckPDU pdu = new AckPDU(ID);

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        assertEquals(OpCode.ACK.value, pdu.toByteArray()[0]);
    }

    @Test
    public void shouldHaveCorrectID() throws Exception {
        byte [] byteArray = pdu.toByteArray();
        short byteId =
                (short) (((((int) byteArray[2]) & 0xff) << 8) +
                        (((int) byteArray[3]) & 0xff));
        assertEquals(ID, byteId);
    }
}