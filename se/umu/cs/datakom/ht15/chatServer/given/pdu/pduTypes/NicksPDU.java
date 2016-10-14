package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.nio.charset.StandardCharsets.UTF_8;
public class NicksPDU extends PDU {

    Set<String> nicknames = new CopyOnWriteArraySet<>();

    public NicksPDU(Collection<String> nicknames){
        this.nicknames = new CopyOnWriteArraySet<>(nicknames);
    }

    public NicksPDU(String... nicknames) {
        for(String s: nicknames){
            this.nicknames.add(s);
        }
    }

    public NicksPDU(InputStream inStream) throws IOException {
        try {
            int nrOfNames = (int) byteArrayToLong(readExactly(inStream,1));
            int length = padLengths((int) byteArrayToLong(readExactly(inStream,2)));
            int howMuchHasBeenRead = 0;
            List<Byte> name = new ArrayList<>();

            nicknames = new CopyOnWriteArraySet<>();

            for(int i=0; i<nrOfNames && howMuchHasBeenRead<length; i++){
                //resets the name
                name.clear();

                byte letter;
                // looks if the current character is \0, if it is, it changes name
                while((letter = readExactly(inStream,1)[0])!=(byte) 0){
                    name.add(letter);
                }

                howMuchHasBeenRead+=name.size()+1;
                //adds the name to the list
                if (name.size()!=0) {
                    byte[] toAdd = new byte[name.size()];
                    for (int j = 0; j < toAdd.length; j++) {
                        toAdd[j] = name.get(j);
                    }
                    nicknames.add(new String(toAdd, UTF_8));
                }
            }

            //reads the rest of the bytes
            int left = length-howMuchHasBeenRead;
            checkPaddedBytes(inStream, left);

        } catch (IOException e) {
            System.err.println("Error in NicksPDU´s constructor");
            throw new IOException();
        }
    }


    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        byte opCode = OpCode.NICKS.value;
        int nickNum = nicknames.size();
        int nameLength = 0;

        //Get length of all nicknames combined
        for (String s : nicknames) {
            byte[] nickUTF =s.getBytes(UTF_8);
            nameLength += nickUTF.length+1;
        }

        bsb.append(opCode,(byte)nickNum);
        bsb.appendShort((short)nameLength);

        //Add the nicknames
        for (String s : nicknames) {
            byte[] nickUTF = new byte[0];
            nickUTF = s.getBytes(UTF_8);

            for (int i = 0; i < nickUTF.length; i++) bsb.append(nickUTF[i]);
            bsb.append((byte)0);
        }

        bsb.pad();

        return bsb.toByteArray();
    }

    public Set<String> getNicknames() {
        return nicknames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NicksPDU nicksPDU = (NicksPDU) o;

        return !(nicknames != null ? !nicknames.equals(nicksPDU.nicknames) : nicksPDU.nicknames != null);

    }
}
