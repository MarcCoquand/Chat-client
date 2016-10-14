package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.ChNickPDU;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.*;

/**
 * Created by marccoquand on 29/09/15.
 */
public class ChNickPDUTest {

    private final String nickname = "Os\u7777ar ";
    private final ChNickPDU pdu = new ChNickPDU(nickname);

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        assertEquals(OpCode.CHNICK.value, pdu.toByteArray()[0]);
    }

    @Test
    public void shouldHaveCorrectNickLength() throws Exception {
        byte expectedLength = (byte)(nickname.getBytes(UTF_8).length);
        assertEquals(expectedLength, pdu.toByteArray()[1]);
    }

    @Test
    public void shouldHaveCorrectNickname() throws Exception {
        byte[] pduBytes = pdu.toByteArray();
        byte[] nickBytes = new byte[pduBytes[1]];
        System.arraycopy(pduBytes, 4, nickBytes, 0, nickBytes.length);
        assertEquals(nickname, new String(nickBytes, UTF_8));
    }

    @Test
    public void shouldHavePaddedEnd() throws Exception {
        byte[] pduBytes = pdu.toByteArray();
        for (int i = 4 + pduBytes[1]; i < pduBytes.length; i++) {
            assertEquals(0, pduBytes[i]);
        }
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws Exception {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }
}