package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;
public class RegPDU extends PDU {

    private String serverName;
    private short port;

    public RegPDU(InputStream inStream) throws IOException {
        try {
            int lengthOfServerName = (int) byteArrayToLong(readExactly(inStream,1));

            short port = (short) byteArrayToLong(readExactly(inStream,2));

            String serverName = getNonPaddedString(inStream,lengthOfServerName);

            this.serverName=serverName;
            this.port=port;

        } catch (IOException e) {
            System.err.println("IO error in RegPDU´s constructor ");
            throw new IOException();
        }

    }


    public RegPDU(String serverName, short port) {
        this.serverName=serverName;
        this.port=port;
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();

        //Adds the REG opCode at the start (1 byte)
        byte opCode = OpCode.REG.value;
        bsb = bsb.append(opCode);

        // adds the length of the serverName (1 byte)
        byte length = (byte) serverName.length();
        bsb.append(length);

        //adds the port (2 bytes)
        bsb.appendShort(this.port);

        //adds the serverName
        byte[] serverUTF =  serverName.getBytes(UTF_8);
        for (int i = 0; i < serverUTF.length; i++){
            bsb.append(serverUTF[i]);
        }

        //fills up the "word"
        bsb.pad();

        return bsb.toByteArray();
    }
}
