package umu.cs.datakom.ht15.chatServer.given.gui;

import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.GetListPDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.RegPDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.SListPDU;
import umu.cs.datakom.ht15.chatServer.given.server.Constants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Class for requesting and receiving server lists.
 * <br/>
 */
public class NameServerModule {

    private List<SListPDU.ServerEntry> serverEntries = new ArrayList<>();
    private Collection<Listener<Collection<String[]>>> listeners =
            new ArrayList<>();
    private String address;
    private int port;
    private Collection<String[]> serverList = new ArrayList<>();
    private Thread listenerThread;

    private class SListListener implements Runnable {
        public void run(){
            byte[] buffer = new byte[65536];
            byte[] getList = new GetListPDU().toByteArray();
            DatagramPacket packet;
            try {
                DatagramSocket socket = new DatagramSocket();
                InetAddress inetAddress = InetAddress.getByName(address);
                socket.setSoTimeout(Constants.WAITTIME);
                socket.connect(inetAddress, port);
                socket.send(new DatagramPacket(getList, getList.length));
                packet = new DatagramPacket(buffer, buffer.length);

                listen(packet, socket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }catch (SocketTimeoutException s) {
                // Give up
                System.out.println("Socket timeout for NameServerModule " + s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listen(DatagramPacket packet, DatagramSocket socket) throws SocketTimeoutException,IOException {
        while (true) {
            //waits until complete package is received
                socket.receive(packet);
                PDU inPdu = PDU.fromInputStream(new ByteArrayInputStream(packet.getData()));
                if (inPdu instanceof SListPDU) {
                    if (((SListPDU) inPdu).getSeqNo() == 0) {
                        serverList = new ArrayList<String[]>();
                    }
                    for (SListPDU.ServerEntry se : ((SListPDU) inPdu).getServerList())
                        serverList.add(toStringArray(se));
                    notifyListeners(serverList);
                }
       }
    }

    private String[] toStringArray(SListPDU.ServerEntry se) {
        String[] sEntry = new String[]{
                se.address.getHostAddress(), Integer.toString((int)se.port & 0xffff), se.serverName,
                Integer.toString((int)se.clientCount & 0xff)
        };
        return  sEntry;
    }

    public void getServerList(String nameServerAddress, int nameServerPort) {
        //byte[] toSend = new GetListPDU().toByteArray();
        address = nameServerAddress;
        port = nameServerPort;
        if (listenerThread != null) listenerThread.interrupt();
        listenerThread = new Thread(new SListListener());
        listenerThread.start();

    }

    /**
     * Adds a listener to be notified when a new server list has been created.
     *
     * @param listener The listener.
     */
    public void addListener(Listener<Collection<String[]>> listener) {
        listeners.add(listener);
    }

    /**
     * Call this when a server list has been received. The entire list in
     * the GUI will be replaced.
     *
     * @param servers The new server list.
     */
    private void notifyListeners(Collection<String[]> servers) {
        for (Listener<Collection<String[]>> listener: listeners) {
            listener.update(servers);
        }

    }
}
