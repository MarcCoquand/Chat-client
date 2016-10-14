package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;

public class NotRegPDU extends PDU {

    //no constructor needed
    public NotRegPDU() {

    }

    public NotRegPDU(InputStream inStream) throws IOException {
        try {
            checkPaddedBytes(inStream, 3);
        } catch (IOException e) {
            System.err.println("Error in inStreamConstructor for GetListPDU");
            throw new IOException();
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.NOTREG.value;

        bsb.append(opCode);
        bsb.pad();

        return bsb.toByteArray();
    }


}
