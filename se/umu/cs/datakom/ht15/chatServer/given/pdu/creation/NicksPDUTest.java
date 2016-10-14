package umu.cs.datakom.ht15.chatServer.given.pdu.creation;

import org.junit.Assert;
import org.junit.Test;
import umu.cs.datakom.ht15.chatServer.given.pdu.AllPDUTests;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.NicksPDU;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class NicksPDUTest {

    private static final NicksPDU THE_PDU =
            new NicksPDU(" Pelle", "M\uabf1ja ", "Bert");
    
    @Test
    public void shouldContainCorrectNicks() throws Exception {
        String[] nicknames = { "Niklas", "Oskar", "Adam" };
        Set<String> nickSet = new HashSet<>();
        Collections.addAll(nickSet, nicknames);
        assertEquals(nickSet, new NicksPDU(nicknames).getNicknames());
    }

    @Test
    public void shouldHaveCorrectOpCode() throws Exception {
        String nickname = "Niklas";
        NicksPDU pdu = new NicksPDU(nickname);
        Assert.assertEquals(OpCode.NICKS.value, pdu.toByteArray()[0]);
    }

    @Test
    public void shouldHaveCorrectLength() throws Exception {
        String nickname = "Niklas";
        byte[] nickBytes = nickname.getBytes(UTF_8);

        NicksPDU pdu = new NicksPDU(nickname);
        Assert.assertEquals(
                PDU.padLengths(4, nickBytes.length),
                pdu.toByteArray().length);
    }

    @Test
    public void shouldHaveCorrectNickCount() throws Exception {
        String[] nicknames = { "Niklas", "Oskar", "Adam" };
        byte[] pduBytes = new NicksPDU(nicknames).toByteArray();
        assertEquals(nicknames.length, pduBytes[1]);
    }

    @Test
    public void shouldHaveCorrectNicksLength() throws Exception {
        String[] nicknames = { "Niklas", "Oskar", "Adam" };
        byte[] pduBytes = new NicksPDU(nicknames).toByteArray();
        int expectedLength = 0;
        for (String nickname: nicknames) {
            expectedLength += nickname.getBytes(UTF_8).length + 1;
        }
        int actualLength = (pduBytes[2] << 8) + pduBytes[3];
        assertEquals(expectedLength, actualLength);
    }

    @Test
    public void shouldBePaddedWithZeros() throws Exception {
        String[] nicknames = { "Niklas", "Oskar", "Adam" };
        byte[] pduBytes = new NicksPDU(nicknames).toByteArray();
        int expectedLength = 0;
        for (String nickname: nicknames) {
            expectedLength += nickname.getBytes(UTF_8).length +1;
        }
        for (int i = 4 + expectedLength; i < pduBytes.length; i++) {
            assertEquals(0, pduBytes[i]);
        }
    }

    @Test
    public void shouldBeEqualWhenReadFromStream() throws IOException {
        AllPDUTests.assertThroughStreamEquals(THE_PDU);
    }
}
