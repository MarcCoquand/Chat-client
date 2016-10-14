package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
public class UCNickPDU extends PDU {

    Date timestamp;
    String oldNick;
    String newNick;

    public UCNickPDU(Date timestamp, String oldNick, String newNick) {
        this.timestamp = timeStampSet(timestamp);
        this.oldNick = oldNick;
        this.newNick = newNick;
    }

    public UCNickPDU(InputStream inStream) throws IOException {
        int nickLength1;
        int nickLength2;
        try{
            nickLength1 = (int) byteArrayToLong(readExactly(inStream, 1));
            nickLength2 = (int) byteArrayToLong(readExactly(inStream, 1));
            /*skipping one pad byte*/
            readExactly(inStream, 1);
            this.timestamp = new Date(1000*byteArrayToLong(readExactly(inStream,4)));
            oldNick = getNonPaddedString(inStream,nickLength1);
            newNick = getNonPaddedString(inStream,nickLength2);
        }
        catch (IOException e){
            System.err.println("Error when initializing UCNickPDU");
            throw new IOException();
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.UCNICK.value;
        //old nickname in utf-8
        byte[] oldNickUTF = oldNick.getBytes(UTF_8);

        //new nickname in utf-8
        byte[] newNickUTF = newNick.getBytes(UTF_8);

        int oldLength = oldNickUTF.length;
        int newLength = newNickUTF.length;
        bsb.append(opCode,(byte)oldLength,(byte)newLength);
        bsb.pad();

        //Timestamp
        bsb.appendInt((int) (timestamp.getTime()/1000));

        //adds the old nickname
        for (int i = 0; i < oldNickUTF.length; i++) bsb.append(oldNickUTF[i]);
        bsb.pad();

        //adds the new nickname
        for (int i = 0; i < newNickUTF.length; i++) bsb.append(newNickUTF[i]);
        bsb.pad();

        return bsb.toByteArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UCNickPDU ucNickPDU = (UCNickPDU) o;

        if (timestamp != null ? !timestamp.equals(ucNickPDU.timestamp) : ucNickPDU.timestamp != null) return false;
        if (oldNick != null ? !oldNick.equals(ucNickPDU.oldNick) : ucNickPDU.oldNick != null) return false;
        return !(newNick != null ? !newNick.equals(ucNickPDU.newNick) : ucNickPDU.newNick != null);

    }

    /**
     * To retrieve the new nickname from the PDU
     * @return the new Nickname
     */
    public String getNewNick() {
        return newNick;
    }

    public String getOldNick() { return oldNick; }
}
