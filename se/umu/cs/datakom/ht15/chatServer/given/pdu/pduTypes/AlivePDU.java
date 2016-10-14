package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;

public class AlivePDU extends PDU {

    private byte clientCount;
    private short id;

    public AlivePDU(InputStream inStream) throws IOException {
        try{
            clientCount = readExactly(inStream, 1)[0];
            id = (short) byteArrayToLong(readExactly(inStream, 2));
        }
        catch (IOException e){
            System.err.println("Error when initializing AlivePDU");
            throw new IOException();
        }
    }

    public AlivePDU(byte clientCount, short id) {
        this.clientCount=clientCount;
        this.id=id;
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.ALIVE.value;

        bsb.append(opCode,clientCount);
        bsb.appendShort(id);

        return bsb.toByteArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlivePDU alivePDU = (AlivePDU) o;

        return id == alivePDU.id;

    }
}
