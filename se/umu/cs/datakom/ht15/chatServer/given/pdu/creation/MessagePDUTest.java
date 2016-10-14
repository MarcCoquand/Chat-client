package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.Checksum;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.MessagePDU;
import org.junit.Assert;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.Checksum;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.MessagePDU;

import java.io.IOException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class MessagePDUTest {

    private static final String NICKNAME = "Nik\u0f10as ";
    private static final String MESSAGE = " Hej \u1123iklas what's up?";
    private static final Date TIMESTAMP = new Date(0x14ed029932cl);
    private static final MessagePDU FROM_CLIENT = new MessagePDU(MESSAGE);
    private static final MessagePDU FROM_SERVER = new MessagePDU(
            MESSAGE,
            NICKNAME,
            TIMESTAMP);
    private static final byte[] NICK_BYTES = NICKNAME.getBytes(UTF_8);
    private static final byte[] MSG_BYTES = MESSAGE.getBytes(UTF_8);
    private static final byte[] CLIENT_BYTES = FROM_CLIENT.toByteArray();
    private static final byte[] SERVER_BYTES = FROM_SERVER.toByteArray();

    @Test
    public void shouldHaveCorrectOpCodeFromClient() throws Exception {
        Assert.assertEquals(OpCode.MESSAGE.value, CLIENT_BYTES[0]);
    }

    @Test
    public void shouldHaveCorrectOpCodeFromServer() throws Exception {
        assertEquals(OpCode.MESSAGE.value, SERVER_BYTES[0]);
    }

    @Test
    public void shouldHaveCorrectLengthFromClient() throws Exception {
        assertEquals(PDU.padLengths(12, MSG_BYTES.length), CLIENT_BYTES.length);
    }

    @Test
    public void shouldHaveCorrectLengthFromServer() throws Exception {
        assertEquals(
                PDU.padLengths(12, MSG_BYTES.length, NICK_BYTES.length),
                SERVER_BYTES.length);
    }

    @Test
    public void shouldHaveZeroNickLengthFromClient() throws Exception {
        assertEquals(0, CLIENT_BYTES[2]);
    }

    @Test
    public void shouldHaveCorrectNickLengthFromServer() throws Exception {
        assertEquals(NICK_BYTES.length, SERVER_BYTES[2]);
    }

    @Test
    public void shouldHaveCorrectMessageLengthFromClient() throws Exception {
        short messageLength =
                (short) (((((int) CLIENT_BYTES[4]) & 0xff) << 8) +
                         (((int) CLIENT_BYTES[5]) & 0xff));
        assertEquals(MSG_BYTES.length, messageLength);
    }

    @Test
    public void shouldHaveCorrectMessageLengthFromServer() throws Exception {
        short messageLength =
                (short) (((((int) SERVER_BYTES[4]) & 0xff) << 8) +
                         (((int) SERVER_BYTES[5]) & 0xff));
        assertEquals(MSG_BYTES.length, messageLength);
    }

    @Test
    public void shouldHavePaddedHeaderFromClient() throws Exception {
        assertEquals(0, CLIENT_BYTES[6]);
        assertEquals(0, CLIENT_BYTES[7]);
    }

    @Test
    public void shouldHavePaddedHeaderFromServer() throws Exception {
        assertEquals(0, SERVER_BYTES[6]);
        assertEquals(0, SERVER_BYTES[7]);
    }

    @Test
    public void shouldHaveZeroTimestampFromClient() throws Exception {
        for (int i = 8; i < 12; i++) {
            assertEquals(0, CLIENT_BYTES[i]);
        }
    }

    @Test
    public void shouldHaveCorrectTimestampFromServer() throws Exception {
        Date cmpDate = new Date((TIMESTAMP.getTime() / 1000) * 1000);
        Date actualDate = new Date(PDU.byteArrayToLong(SERVER_BYTES, 8, 12) * 1000);
        assertEquals(cmpDate, actualDate);
    }

    @Test
    public void shouldHavePaddedMessageFromClient() throws Exception {
        for (int i = 12 + MSG_BYTES.length;
             i < PDU.padLengths(12, MSG_BYTES.length);
             i++) {
            assertEquals(0, CLIENT_BYTES[i]);
        }
    }

    @Test
    public void shouldHavePaddedMessageFromServer() throws Exception {
        for (int i = 12 + MSG_BYTES.length;
             i < PDU.padLengths(12, MSG_BYTES.length);
             i++) {
            assertEquals(0, SERVER_BYTES[i]);
        }
    }

    @Test
    public void shouldHavePaddedNickFromServer() throws Exception {
        int msgStart = PDU.padLengths(12, MSG_BYTES.length);
        int padStart = msgStart + NICK_BYTES.length;
        int totalLength = msgStart + PDU.padLengths(NICK_BYTES.length);
        for (int i = padStart; i < totalLength; i++) {
            assertEquals(0, SERVER_BYTES[i]);
        }
    }

    @Test
    public void shouldBeEqualWhenClientMsgReadFromStream() throws IOException {
        AllPDUTests.assertThroughStreamEquals(FROM_CLIENT);
    }

    @Test
    public void shouldBeEqualWhenServerMsgReadFromStream() throws IOException {
        AllPDUTests.assertThroughStreamEquals(FROM_SERVER);
    }

    @Test
    public void shouldHaveCorrectCheckSumFromClient() throws Exception {
        Assert.assertEquals(0, Checksum.computeChecksum(CLIENT_BYTES));
    }

    @Test
    public void shouldHaveCorrectCheckSumFromServer() throws Exception {
        assertEquals(0, Checksum.computeChecksum(SERVER_BYTES));
    }
}