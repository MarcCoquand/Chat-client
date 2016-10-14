package umu.cs.datakom.ht15.chatServer.given.pdu;

import sun.misc.IOUtils;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;
/**
 * Super class of all PDUs with methods for reading PDUs from InputStreams.
 */
public abstract class PDU {


    private static OpCode getOPCodeValue(int value) {
        for(OpCode oc : OpCode.values()) {
            if (oc.value == value) return oc;
        }
        throw new IllegalArgumentException("Invalid OPCode received: " + value);
    }

    /**
     * Reads the OpCode from the InputStream and determines the type of
     * the PDU. Then a PDU of the correct subclass is read from the stream.
     *
     * @param inStream The InputStream to read the PDU from.
     * @return The read PDU.
     * @throws java.io.IOException If an IOException was thrown when reading from the
     *          stream.
     * @throws IllegalArgumentException If the first byte of the stream
     *          doesn't represent a correct OpCode.
     */
    public static PDU fromInputStream(InputStream inStream) throws IOException {
        PDU typeOfPDU;

        //reads the Op-code
        byte[] input = readExactly(inStream,1);
        int number = (int) byteArrayToLong(input);
        OpCode oc = getOPCodeValue(number);

        switch(oc){
            case REG:
                typeOfPDU= new RegPDU(inStream);
                break;
            case ACK:
                typeOfPDU = new AckPDU(inStream);
                break;
            case ALIVE:
                typeOfPDU = new AlivePDU(inStream);
                break;
            case NOTREG:
                typeOfPDU = new NotRegPDU(inStream);
                break;
            case GETLIST:
                typeOfPDU = new GetListPDU(inStream);
                break;
            case SLIST:
                typeOfPDU = new SListPDU(inStream);
                break;
            case JOIN:
                typeOfPDU = new JoinPDU(inStream);
                break;
            case NICKS:
                typeOfPDU = new NicksPDU(inStream);
                break;
            case QUIT:
                typeOfPDU = new QuitPDU(inStream);
                break;
            case MESSAGE:
                typeOfPDU = new MessagePDU(inStream);
                break;
            case CHNICK:
                typeOfPDU = new ChNickPDU(inStream);
                break;
            case UJOIN:
                typeOfPDU = new UJoinPDU(inStream);
                break;
            case ULEAVE:
                typeOfPDU = new ULeavePDU(inStream);
                break;
            case UCNICK:
                typeOfPDU = new UCNickPDU(inStream);
                break;
            default:
                //TODO: Result with quit
                throw new IOException();
        }

        return typeOfPDU;
    }

    /**
     *
     * @param inStream
     * @param length - non-padded length
     * @return the non-padded string
     * @throws IOException
     */
    protected String getNonPaddedString(InputStream inStream, long length) throws IOException {
        return getNonPaddedString(inStream,(int) length);
    }

    /**
     * Reads length number of bytes from inputStream and if they are non-zero
     * we throw an exception
     * @param inStream the inStream to read from
     * @param length how many bytes to read
     * @throws IOException if the read bytes are non zero
     */
    protected byte[] checkPaddedBytes(InputStream inStream, int length) throws IOException{
        return checkPaddedBytes(readExactly(inStream,length));
    }

    /**
     * Reads length number of bytes from inputStream and if they are non-zero
     * we throw an exception
     * @param check a byteArray of the read bytes
     * @throws IOException if the read bytes are non zero
     */
    protected byte[] checkPaddedBytes(byte[] check) throws IOException{
        for (int i = 0; i < check.length; i++) {
            if (check[i] != (byte) 0){
                throw new IOException();
            }
        }
        return check;
    }

    /**
     *
     * @param inStream
     * @param length - non-padded length
     * @return the non-padded string
     * @throws IOException
     */
    protected String getNonPaddedString(InputStream inStream, int length) throws IOException {
        byte[] input = readExactly(inStream, padLengths(length));
        byte[] stringWithoutPad = new byte[length];
        System.arraycopy(input,0,stringWithoutPad,0,length);
        return new String(stringWithoutPad,UTF_8);
    }

    /**
     * @return A byte array representation of the PDU with a length divisible
     * by 4.
     */
    public abstract byte[] toByteArray();

    public static Date byteArrayToDate(InputStream inStream) {
        try {
            long seconds = byteArrayToLong(readExactly(inStream,4));
            return new Date(((seconds)) * 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Takes a timestamp and returns the timestamp rounded to the closest second
     * @param timestamp
     * @return
     */
    protected Date timeStampSet(Date timestamp) {
        int timeInSeconds = (int) (timestamp.getTime() / 1000);
        return new Date((long) timeInSeconds * 1000);
    }

    /**
     * Computes the sum of the specified lengths, padding each
     * individually to be divisible by 4.
     *
     * @param lengths The lengths to pad.
     * @return The padded sum.
     */
    public static int padLengths(int... lengths) {
        int result = 0;
        for (int length : lengths) {
            if (length % 4 != 0) {
                result += length + (4 - (length % 4));
            } else {
                result += length;
            }
        }
        return result;
    }

    /**
     * Reads exactly the specified amount of bytes from the stream, blocking
     * until they are available even though some bytes are.
     *
     * @param is The InputStream to read from.
     * @param len The number of bytes to read.
     * @return A byte array containing the read bytes.
     * @throws IllegalArgumentException If the number of bytes to read is
     *                      negative.
     * @throws java.io.IOException If an IOException was thrown when reading from the
     *                      stream.
     */
    public static byte[] readExactly(InputStream is, int len)
            throws IOException {

        if (len < 0) {
            throw new IllegalArgumentException("Negative length to read");
        }
        byte[] buffer = new byte[len];

        int readCount = 0;
        while (readCount < len) {
            int readBytes = is.read(buffer, readCount, len - readCount);
            readCount += readBytes;
        }

        return buffer;
    }

    public static long byteArrayToLong(byte[] bytes) {
        return byteArrayToLong(bytes, 0, bytes.length);
    }

    public static long byteArrayToLong(byte[] bytes, int start, int stop) {
        if (stop - start > 8) {
            throw new IllegalArgumentException(
                    "Byte array can't have more than 8 bytes");
        }
        long result = 0;
        for (int i = start; i < stop; i++) {
            result <<= 8;
            result += ((long) bytes[i]) & 0xff;
        }
        return result;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }
}
