package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.ULeavePDU;

import java.util.Date;

import static org.junit.Assert.*;

import static java.nio.charset.StandardCharsets.UTF_8;
/**
 * Created by marccoquand on 29/09/15.
 */
public class ULeavePDUTest {
    private static final Date TIMESTAMP = new Date(0x14ed029932cl);
    private static final String NICKNAME = "Os\u7777ar ";
    private static final ULeavePDU pdu = new ULeavePDU(NICKNAME, TIMESTAMP);

    @Test
    public void shouldHaveCorrectOpCOde() throws Exception {
        assertEquals(OpCode.ULEAVE.value, pdu.toByteArray()[0]);
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
        assertEquals((int) (TIMESTAMP.getTime() / 1000), pdu.byteArrayToLong(
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
    public void shouldBeEqualWhenReadFromStream() throws Exception {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }

}