package umu.cs.datakom.ht15.chatServer.given.pdu.streaming;

import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import org.junit.Before;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class StreamTest {

    private InputStream inputStream;
    private OutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        Socket inSocket = new Socket("localhost", serverSocket.getLocalPort());
        Socket outSocket = serverSocket.accept();
        inputStream = inSocket.getInputStream();
        outputStream = outSocket.getOutputStream();
    }


    @Test(timeout = 10000)
    public void shouldReadOnePDUOneByteAtATime() throws Exception {
        PDU pdu = new NicksPDU("Pelle", "Johan", "Anna");
        new Thread(
                () -> {
                    try {
                        byte[] pduBytes = pdu.toByteArray();
                        for (byte b : pduBytes) {
                            outputStream.write(b);
                            outputStream.flush();
                            Thread.sleep(50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Rethrown");
                    }
                }).start();
        PDU inPdu = PDU.fromInputStream(inputStream);
        assertEquals(pdu, inPdu);
    }

    @Test(timeout = 10000)
    public void shouldReadSeveralConsecutivePDUs() throws Exception {
        PDU[] pdus = {
                new QuitPDU(),
                new JoinPDU("Berta"),
                new MessagePDU("Hej", "Anna", new Date(765485435)),
                new ChNickPDU("Ragnar"),
                new AckPDU((short) 100)
        };
        for (PDU pdu: pdus) {
            outputStream.write(pdu.toByteArray());
        }
        outputStream.flush();


        int wrong=0;

        for (PDU pdu: pdus) {
            //(pdu, PDU.fromInputStream(inputStream));
            byte[] a = pdu.toByteArray();
            if (!pdu.equals(PDU.fromInputStream(inputStream))){
                wrong++;
            }
        }

        assert(wrong==0);
    }
}
