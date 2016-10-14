package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
public class ULeavePDU extends PDU {

    String nickname;
    Date timestamp;

    public ULeavePDU(String nickname, Date timestamp) {
        this.nickname = nickname;
        this.timestamp = timeStampSet(timestamp);
    }

    public ULeavePDU(InputStream inStream) throws IOException {

        try{
            int nickLength = (int) byteArrayToLong(readExactly(inStream, 1));
            /*skipping two pad bytes*/
            checkPaddedBytes(inStream, 2);
            this.timestamp = new Date(1000*byteArrayToLong(readExactly(inStream,4)));
            nickname = getNonPaddedString(inStream,nickLength);
        }
        catch (IOException e){
            System.err.println("Error when initializing ULeavePDU");
            throw new IOException();
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.ULEAVE.value;

        //adds the nickname
        byte[] nickUTF = nickname.getBytes(UTF_8);

        bsb.append(opCode,(byte)(nickUTF.length));
        bsb.pad();

        //Timestamp
        bsb.appendInt((int) (timestamp.getTime()/1000));


        for (int i = 0; i < nickUTF.length; i++) bsb.append(nickUTF[i]);
        bsb.pad();

        return bsb.toByteArray();
    }

    public String getNickname() {
        return nickname;
    }
}
