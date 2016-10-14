package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;

public class AckPDU extends PDU {
    private short id;

    public AckPDU(short id) {
        this.id = id;
    }

    public AckPDU (InputStream inStream) throws IOException {
        byte[] input;
        try {
            // First byte is pad
            checkPaddedBytes(inStream, 1);
            // Second and third byte is the short
            input = readExactly(inStream, 2);
            id = (short)byteArrayToLong(input);
        } catch (IOException e) {
            System.err.println("input error in AckPDU");
            throw new IOException();
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.ACK.value;
        byte pad = 0;
        bsb.append(opCode,pad);
        bsb.appendShort(id);
        return bsb.toByteArray();
    }

    public short getUniqueID(){
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AckPDU ackPDU = (AckPDU) o;

        return id == ackPDU.id;
    }
}
