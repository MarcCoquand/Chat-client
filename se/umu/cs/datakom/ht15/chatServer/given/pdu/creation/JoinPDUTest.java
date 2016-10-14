package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import org.junit.Assert;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.JoinPDU;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class JoinPDUTest {

    private static final String NICKNAME = "Os\uabcdar ";
    private final JoinPDU pdu = new JoinPDU(NICKNAME);

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        Assert.assertEquals(OpCode.JOIN.value, pdu.toByteArray()[0]);
    }
    
    @Test
    public void shouldHaveCorrectNickLength() throws Exception {
        assertEquals(NICKNAME.getBytes(UTF_8).length, pdu.toByteArray()[1]);
    }

    @Test
    public void shouldBePaddedFirstWord() throws Exception {
        byte[] pduBytes = pdu.toByteArray();
        assertEquals(0, pduBytes[2]);
        assertEquals(0, pduBytes[3]);
    }

    @Test
    public void shouldHaveCorrectLength() throws Exception {
        byte[] nickBytes = NICKNAME.getBytes(UTF_8);
        assertEquals(
                PDU.padLengths(4, nickBytes.length),
                pdu.toByteArray().length);
    }

    @Test
    public void shouldBePaddedWithZeros() {
        byte[] nickBytes = NICKNAME.getBytes(UTF_8);
        for (int i = 4 + nickBytes.length; i < pdu.toByteArray().length; i++) {
            assertEquals(0, pdu.toByteArray()[i]);
        }
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws Exception {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }
}
