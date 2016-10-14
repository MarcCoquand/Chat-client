package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;


import static umu.cs.datakom.ht15.chatServer.given.pdu.Checksum.computeChecksum;

import static java.nio.charset.StandardCharsets.UTF_8;
/**
 * Subclass of PDU representing a PDU with a time stamp, a message and a
 * sender nickname.
 */
public class MessagePDU extends PDU {

    private String message;
    private Date timestamp;
    private String nickname;

    public MessagePDU(String message) {
        this.message = message;
        //assumes no nickname
        this.nickname = "";
        //sends timestamp 1970
        this.timestamp= new Date(0);
    }

    public MessagePDU(
            String message,
            String nickname,
            Date timestamp) {
        this.message=message;
        this.nickname = nickname;
        this.timestamp=timeStampSet(timestamp);
    }

    public MessagePDU(InputStream inStream) throws IOException {
        byte[] input;
        int nickLength;
        int messageLength;

        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        bsb.append(OpCode.MESSAGE.value);

        try {
            // Second byte is pad
            bsb.append(checkPaddedBytes(inStream, 1));
            // third byte is nickLength
            input = readExactly(inStream, 1);
            bsb.append(input);
            nickLength = (int)byteArrayToLong(input);

            //fourth is a checksum
            bsb.append(readExactly(inStream,1));

            //fifth and sixth are messageLength (short ironically)
            input = readExactly(inStream,2);
            bsb.append(input);
            messageLength = (int) byteArrayToLong(input);

            //two pads
            bsb.append(checkPaddedBytes(inStream,2));

            //the date
            long time = byteArrayToLong(readExactly(inStream, 4));
            bsb.appendInt((int)time);

            timestamp= new Date(time*1000);

            message = getNonPaddedString(inStream,messageLength);
            nickname = getNonPaddedString(inStream,nickLength);

            /*
            input = readExactly(inStream, padLengths(messageLength));
            bsb.append(input);
            byte[] messageWithoutPad = new byte[messageLength];
            System.arraycopy(input,0,messageWithoutPad,0,messageLength);
            message = new String(messageWithoutPad,UTF_8);

            input = readExactly(inStream,padLengths(nickLength));
            bsb.append(input);
            byte[] nicknameWithPad = new byte[nickLength];
            System.arraycopy(input,0,nicknameWithPad,0,nickLength);
            nickname = new String(nicknameWithPad,UTF_8);
            */

            assert(computeChecksum(bsb.toByteArray())==0);

        } catch (IOException e) {
            System.err.println("input error in ACK");
            throw new IOException();
        }
    }

    public String getMessage(){
        return message;
    }
    public String getNickname() { return nickname; }
    public Date getDate(){ return timestamp; }

    @Override
    public String toString(){
        String stringed;
        if (!getNickname().equals("")) {
            stringed = getNickname() + ":\n   " + getMessage() + "\nsent at: " + getDate().toString() + "\n";
        }
        else{
            stringed = "    Message from server:\n    "+getMessage()+"\n    sent at:" + getDate().toString() +"\n";
        }

        return stringed;
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.MESSAGE.value;


        byte[] nickUTF =  nickname.getBytes(UTF_8);


        // No \0 termination
        byte nickLength = (byte)(nickUTF.length);


        byte[] messUTF =  message.getBytes(UTF_8);


        // No \0 termination
        short messLength = (short)(messUTF.length);

        //Adds nickname length and checksum
        bsb.append(opCode);
        bsb.append((byte) 0);

        /* the zero will be changed to the checksum after the array is built */
        bsb.append(nickLength, (byte) 0);

        bsb.appendShort(messLength);
        bsb.pad();


        int p = (int) (timestamp.getTime()/1000);
        //Timestamp
        bsb.appendInt(p);

        //Adds the message
        for (int i = 0; i < messLength; i++) bsb.append(messUTF[i]);
        bsb.pad();

        //adds the nickname
        for (int i = 0; i < nickLength; i++) bsb.append(nickUTF[i]);
        bsb.pad();

        //computing checkSum over entire message
        byte[] message = bsb.toByteArray();
        //inserting the checksum at its position
        message[3]=computeChecksum(message);

        return message;
    }
}
