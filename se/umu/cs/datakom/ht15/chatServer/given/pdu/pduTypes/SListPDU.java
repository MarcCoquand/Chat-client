package umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes;

import umu.cs.datakom.ht15.chatServer.given.pdu.ByteSequenceBuilder;
import umu.cs.datakom.ht15.chatServer.given.pdu.OpCode;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
public class SListPDU extends PDU {
    private byte seqNo;
    private List<ServerEntry> serverList= new ArrayList<>();


    public SListPDU(byte seqNo, ServerEntry... entries) {
        this.seqNo=seqNo;
        for(ServerEntry e: entries){
            serverList.add(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SListPDU sListPDU = (SListPDU) o;

        if (seqNo != sListPDU.seqNo) return false;
        return !(serverList != null ? !serverList.equals(sListPDU.serverList) : sListPDU.serverList != null);

    }
    
    

    public SListPDU(InputStream inStream) throws IOException {
        short nrOfServers;
        InetAddress address;
        short port;
        byte clientCount;
        long serverNameLength;
        String serverName;

        try {

            seqNo=readExactly(inStream,1)[0];
            nrOfServers= (short) byteArrayToLong(readExactly(inStream,2));

            for(int i=0; i<nrOfServers; i++){
                address = InetAddress.getByAddress(readExactly(inStream,4));
                port = (short) byteArrayToLong(readExactly(inStream,2));
                clientCount = readExactly(inStream,1)[0];

                //reads with the pad but only saves the non-padded part
                serverNameLength = byteArrayToLong(readExactly(inStream, 1));
                serverName = getNonPaddedString(inStream, serverNameLength);

                serverList.add(new ServerEntry(address,port,clientCount,serverName));
            }

        } catch (IOException e) {
            System.err.println("input error in AckPDUs constructor");
            throw new IOException();
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteSequenceBuilder bsb = new ByteSequenceBuilder();
        short serverNo = (short) serverList.size();
        byte opCode = OpCode.SLIST.value;
        bsb.append(opCode, seqNo);
        bsb.appendShort(serverNo);

        for (ServerEntry se : serverList) {
            byte[] address=  se.address.getAddress();

            //Server name in utf-8
            byte[] serverName =  se.serverName.getBytes(UTF_8);

            //adds the address
            for (int i = 0; i < address.length; i++) bsb.append(address[i]);
            bsb.pad();

            bsb.appendShort(se.port);
            bsb.append(se.clientCount, (byte)serverName.length);

            //adds the server
            for (int i = 0; i < serverName.length; i++) bsb.append( serverName[i]);
            bsb.pad();
        }
        return bsb.toByteArray();
    }

    public byte getSeqNo() {
        return seqNo;
    }

    public List<ServerEntry> getServerList() {
        return serverList;
    }

    public static class ServerEntry {

        public final InetAddress address;
        public final short port;
        public final byte clientCount;
        public final String serverName;

        public ServerEntry(
                InetAddress address,
                short port,
                byte clientCount,
                String serverName) {
            this.serverName = serverName;
            this.address = address;
            this.port = port;
            this.clientCount = clientCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ServerEntry that = (ServerEntry) o;

            if (port != that.port) return false;
            if (clientCount != that.clientCount) return false;
            if (address != null ? !address.equals(that.address) : that.address != null) return false;
            return !(serverName != null ? !serverName.equals(that.serverName) : that.serverName != null);

        }
    }
}
