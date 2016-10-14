package umu.cs.datakom.ht15.chatServer.given.pdu;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import umu.cs.datakom.ht15.chatServer.given.pdu.creation.*;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.AlivePDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.streaming.StreamTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("OverlyCoupledClass")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        JoinPDUTest.class,
        MessagePDUTest.class,
        NicksPDUTest.class,
        SListPDUTest.class,
        UJoinPDUTest.class,
        ULeavePDUTest.class,
        AckPDUTest.class,
        AlivePDUTest.class,
        ChNickPDUTest.class,
        NotRegPDUTest.class,
        UCNickPDUTest.class,
        StreamTest.class
})
public class AllPDUTests {

    public static void assertThroughStreamEquals(PDU pdu) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(pdu.toByteArray());
        PDU inPdu = PDU.fromInputStream(
                new ByteArrayInputStream(
                        outputStream.toByteArray()));
        assertEquals(pdu, inPdu);
    }
}
