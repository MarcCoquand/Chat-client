package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.UCNickPDU;

import java.util.Date;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.*;

/**
 * Created by marccoquand on 29/09/15.
 */
public class UCNickPDUTest {
    private static final String OLDNICK = "Tho\u7777mas";
    private static final String NEWNICK = "Os\u7777ar ";
    private static final Date TIMESTAMP = new Date(0x14ed029932cl);
    private static final UCNickPDU pdu = new UCNickPDU(TIMESTAMP,OLDNICK,NEWNICK);

    @Test
    public void shouldHaveCorrectOpCOde() throws Exception {
        assertEquals(OpCode.UCNICK.value, pdu.toByteArray()[0]);
    }

    @Test
    public void shouldHaveCorrectOldNicknameLength() throws Exception {
        assertEquals(OLDNICK.getBytes(UTF_8).length, pdu.toByteArray()[1]);
    }

    @Test
    public void shouldHaveCorrectNewNicknameLength() throws Exception {
        assertEquals(NEWNICK.getBytes(UTF_8).length, pdu.toByteArray()[2]);
    }

    @Test
    public void shouldHaveCorrectTimeStamp() throws Exception {
        byte[] timeBytes = new byte[4];
        System.arraycopy(pdu.toByteArray(), 4, timeBytes, 0, 4);
        assertEquals((int) (TIMESTAMP.getTime() / 1000), pdu.byteArrayToLong(
                timeBytes));
    }

    @Test
    public void shouldHaveCorrectOldNick() throws Exception {
        byte[] pduBytes = pdu.toByteArray();
        byte[] nickBytes = new byte[pduBytes[1]];
        System.arraycopy(pduBytes, 8, nickBytes, 0, nickBytes.length);
        assertEquals(OLDNICK, new String(nickBytes, UTF_8));
    }

    @Test
    public void shouldHaveCorrectNewNick() throws Exception {
        byte[] pduBytes = pdu.toByteArray();
        byte[] nickBytes = new byte[pduBytes[2]];
        //från 20:e sätter det i nickBytes
        System.arraycopy(pduBytes, 20, nickBytes, 0, nickBytes.length);
        assertEquals(NEWNICK, new String(nickBytes, UTF_8));
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws Exception {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }
}