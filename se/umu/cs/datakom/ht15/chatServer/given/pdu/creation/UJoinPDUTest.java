package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.UJoinPDU;

import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class UJoinPDUTest {

    private static final Date JOIN_TIME = new Date(16038962198l);
    private static final String NICKNAME = "Os\u7777ar ";
    private static final UJoinPDU pdu = new UJoinPDU(NICKNAME, JOIN_TIME);

    @Test
    public void shouldHaveCorrectOpCOde() throws Exception {
        assertEquals(OpCode.UJOIN.value, pdu.toByteArray()[0]);
    }

    @Test
    public void shouldHaveCorrectNicknameLength() throws Exception {
        assertEquals(NICKNAME.getBytes(UTF_8).length, pdu.toByteArray()[1]);
    }

    @Test
    public void shouldHavePaddedHeader() throws Exception {
        assertEquals(0, pdu.toByteArray()[2]);
        assertEquals(0, pdu.toByteArray()[3]);
    }

    @Test
    public void shouldHaveCorrectTimeStamp() throws Exception {
        byte[] timeBytes = new byte[4];
        System.arraycopy(pdu.toByteArray(), 4, timeBytes, 0, 4);
        assertEquals((int) (JOIN_TIME.getTime() / 1000), PDU.byteArrayToLong(
                        timeBytes));
    }

    @Test
    public void shouldHaveCorrectNickname() throws Exception {
        byte[] pduBytes = pdu.toByteArray();
        byte[] nickBytes = new byte[pduBytes[1]];
        System.arraycopy(pduBytes, 8, nickBytes, 0, nickBytes.length);
        assertEquals(NICKNAME, new String(nickBytes, UTF_8));
    }

    @Test
    public void shouldHaveCorrectLength() throws Exception {
        assertEquals(
                8 + PDU.padLengths(NICKNAME.getBytes(UTF_8).length),
                pdu.toByteArray().length);
    }

    @Test
    public void shouldHavePaddedEnd() throws Exception {
        byte[] pduBytes = pdu.toByteArray();
        for (int i = 8 + pduBytes[1]; i < pduBytes.length; i++) {
            assertEquals(0, pduBytes[i]);
        }
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws Exception {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }
}