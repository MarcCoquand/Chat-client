package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.RegPDU;

import static org.junit.Assert.*;

/**
 * Created by marccoquand on 29/09/15.
 */
public class RegPDUTest {
    private final RegPDU pdu = new RegPDU("He\u7777j",(byte)0);

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        assertEquals(OpCode.REG.value, pdu.toByteArray()[0]);
    }

}