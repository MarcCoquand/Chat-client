package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;


import static java.nio.charset.StandardCharsets.UTF_8;
public class JoinPDU extends PDU {

    String nickname;

    public JoinPDU(InputStream inStream) throws IOException {
        try {
            long length = byteArrayToLong(readExactly(inStream, 1));
            checkPaddedBytes(inStream, 2);
            nickname = getNonPaddedString(inStream,length);
        }
        catch (IOException e){
            System.err.println("IOException in CHNickPDUs constructor");
            throw new IOException();
        }
    }

    public String getNickname(){
        return nickname;
    }

    public JoinPDU(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.JOIN.value;
        byte[] nickUTF = nickname.getBytes(UTF_8);
        byte nickLength = (byte)nickUTF.length;

        bsb.append(opCode,nickLength);
        bsb.pad();

        //adds the nickname
        for (int i = 0; i < nickLength; i++){
            bsb.append(nickUTF[i]);
        }

        bsb.pad();

        return bsb.toByteArray();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinPDU joinPDU = (JoinPDU) o;

        return !(nickname != null ? !nickname.equals(joinPDU.nickname) : joinPDU.nickname != null);

    }
}
