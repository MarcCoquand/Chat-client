package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.SListPDU;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.SListPDU;

import java.io.IOException;
import java.net.InetAddress;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class SListPDUTest {

    private static final byte SEQ_NO = (byte) 183;
    private SListPDU pdu;
    private SListPDU.ServerEntry[] entries;
    private byte[] pduBytes;

    @Before
    public void setUp() throws Exception {
        entries = new SListPDU.ServerEntry[]{
                new SListPDU.ServerEntry(
                        InetAddress.getByAddress(new byte[]{1, 2, 3, 4}),
                        (short) 5213,
                        (byte) 218,
                        "Datas\u1234ack"
                ),
                new SListPDU.ServerEntry(
                        InetAddress.getByAddress(
                                new byte[]{1, 2, 3, (byte) 212}),
                        (short) 312,
                        (byte) 21,
                        "SUPER\u789aEGAMINGEL!"
                ),
                new SListPDU.ServerEntry(
                        InetAddress.getByAddress(
                                new byte[]{(byte) 218, 9, 34, 1}),
                        (short) 78,
                        (byte) 1,
                        "Du\u1253no"
                )};
        pdu = new SListPDU(SEQ_NO, entries);
        pduBytes = pdu.toByteArray();
    }

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        Assert.assertEquals(OpCode.SLIST.value, pduBytes[0]);
    }

    @Test
    public void shouldHaveCorrectLength() {
        int length = 4;
        for (SListPDU.ServerEntry entry : entries) {
            length += 8;
            length += PDU.padLengths(entry.serverName.getBytes(UTF_8).length);
        }
        assertEquals(length, pduBytes.length);
    }

    @Test
    public void shouldHaveCorrectSeqNo() {
        assertEquals(SEQ_NO, pduBytes[1]);
    }

    @Test
    public void shouldHaveCorrectServerCount() {
        assertEquals(entries.length, PDU.byteArrayToLong(pduBytes, 2, 4));
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws IOException {
        AllPDUTests.assertThroughStreamEquals(pdu);
    }
}