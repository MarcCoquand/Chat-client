package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
public class UJoinPDU extends PDU {

    String nickname;
    Date timestamp;

    public UJoinPDU(String nickname, Date timestamp) {
        this.nickname = nickname;
        this.timestamp = timeStampSet(timestamp);
    }


    public UJoinPDU(InputStream inStream) throws IOException  {
        try{
            int nickLength = (int) byteArrayToLong(readExactly(inStream, 1));
            /*skipping two padded bytes*/
            checkPaddedBytes(inStream, 2);
            this.timestamp = byteArrayToDate(inStream);
            nickname = getNonPaddedString(inStream,nickLength);
        }
        catch (IOException e){
            System.err.println("Error when initializing UJoinDU");
            throw new IOException();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UJoinPDU uJoinPDU = (UJoinPDU) o;

        if (nickname != null ? !nickname.equals(uJoinPDU.nickname) : uJoinPDU.nickname != null) return false;
        return !(timestamp != null ? !timestamp.equals(uJoinPDU.timestamp) : uJoinPDU.timestamp != null);
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.UJOIN.value;
        byte[] nickUTF = nickname.getBytes(UTF_8);
        byte nickLength = (byte)nickUTF.length;

        bsb.append(opCode, nickLength);
        bsb.pad();

        int p = (int) (timestamp.getTime()/1000);
        bsb.appendInt(p);

        //adds the nickname
        for (int i = 0; i < nickLength; i++){
            bsb.append(nickUTF[i]);
        }

        bsb.pad();

        return bsb.toByteArray();
    }

    public String getNickname() {
        return nickname;
    }
}
