package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static java.nio.charset.StandardCharsets.UTF_8;
public class ChNickPDU extends PDU {

    private String nickname;

    public ChNickPDU(InputStream inStream) throws IOException {
        try {
            int length = (int) byteArrayToLong(readExactly(inStream, 1));
            checkPaddedBytes(inStream, 2);
            nickname = getNonPaddedString(inStream,length);
        }
        catch (IOException e){
            System.err.println("Invalid creation in CHNickPDU");
            throw new IOException();
        }
    }

    public ChNickPDU(String nickname) {
        this.nickname=nickname;
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.CHNICK.value;
        byte[] nickUTF = nickname.getBytes(UTF_8);
        int nickLength = nickUTF.length;
        bsb.append(opCode,(byte)nickLength);
        bsb.pad();


        //adds the nickname
        for (int i = 0; i < nickUTF.length; i++) bsb.append(nickUTF[i]);

        bsb.pad();

        return bsb.toByteArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChNickPDU chNickPDU = (ChNickPDU) o;

        return !(nickname != null ? !nickname.equals(chNickPDU.nickname) : chNickPDU.nickname != null);

    }

    public String getNickname() {
        return nickname;
    }
}
