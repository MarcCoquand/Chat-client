package umu.cs.datakom.ht15.chatServer.given.server;
import umu.cs.datakom.ht15.chatServer.given.gui.ChatModule;
import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.*;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Server {

    private InetAddress nameServerAddress;
    private short nameServerPort;
    private ServerSocket serverSocket;
    private DatagramSocket dSocket = null;
    private short uniqueID;
    private boolean registered=false;
    private List<ClientInfo> clients = new CopyOnWriteArrayList<>();

    public Server(String address, short nameServerPort){

        setUp(address,nameServerPort);

        listenNameServer();
        sendNameServer();

        acceptNewClients();
        //passMessages(inStream); we need a stream that everyone writes messages to
        //listen to clients, one thread per client

    }

    /**
     * Listens for pdu:s, if it is a message, it sends it to everyone
     * remark: Should be started on its own thread as in newClient
     * @param client the client to listen to
     */
    private void listenToClient(ClientInfo client){
        boolean registered=true;
        while(registered) {
            PDU pdu = null;
            try {
                pdu = PDU.fromInputStream(getInputStreamFromClient(client));
            } catch (IOException e) {
                System.err.println("Invalid OP-code, should disconnect client");
            }

            if (pdu instanceof MessagePDU) {
                //sends the message it got but sets the nickname and the date correctly (never trust a client)
                String message = ((MessagePDU) pdu).getMessage();
                Date date = new Date();
                MessagePDU toSend = new MessagePDU(message,client.getNickName(),date);
                System.out.println("Sending message: '"+message+"' from client: '"+client.getNickName()+"'\n" +
                        "at time '"+date+"'");
                sendToAll(toSend);
            }
            else if (pdu instanceof ChNickPDU){
                String newNickName = ((ChNickPDU) pdu).getNickname();
                if (!getClientsNickNames().contains(newNickName)) {
                    System.out.println("Saying to everyone that '"+client.getNickName()+"' has changed name to " +
                            "'"+newNickName+"'");
                    sendTCP(client.getSocket(), new UCNickPDU(new Date(), client.getNickName(), newNickName));
                    client.setNickName(newNickName);
                }
                else{
                    //sending back, the old name as the new so she does not change name
                   sendTCP(client.getSocket(),new UCNickPDU(new Date(),client.getNickName(),client.getNickName()));
                }
            }
            else if (pdu instanceof QuitPDU){
                clients.remove(client);
                System.out.println("Client: '" + client.getNickName() + "' disconnected to the server");
                sendToAll(new ULeavePDU(client.getNickName(), new Date(0)));
                registered=false;
            }
            else{
                sendTCP(client.getSocket(),new QuitPDU().toByteArray());
                clients.remove(client);
                sendToAll(new ULeavePDU(client.getNickName(), new Date(0)));
                registered=false;
                System.err.println("Removed the client: '"+client.getNickName()+"' because of incorrect OP-code");
            }
        }
    }

    /**
     * Sets up all connection to the nameServer and opens a ServerSocket to connect to
     */
    private void setUp(String address, short nameServerPort) {


        //To nameServer
        try {
            dSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Could not start a socket");
        }

        try {
            nameServerAddress = InetAddress.getByName(address);
            this.nameServerPort = nameServerPort;
            dSocket.connect(nameServerAddress, this.nameServerPort);
        } catch (UnknownHostException e) {
            System.err.println("No host on address '" + address + "' and port: "+ this.nameServerPort);
        }
        //to clients
        try {
            serverSocket = new ServerSocket(this.nameServerPort);
        } catch (IOException e) {
            System.out.println("Error when starting the serverSocket");
        }
    }

    /**
     * Starts a thread that accepts invitations to tcp
     * this does not mean they are a client yet, for that
     * they have to send the joinPDU
     */
    private void acceptNewClients(){

        new Thread() {
            public void run() {
                while(true){
                    try {
                        Socket socket = (serverSocket.accept());
                        System.out.println("A socket connected from server: \n'"
                            +socket.getInetAddress()+"' at port: '"+socket.getPort()+"'");
                        newClient(socket);
                    } catch (IOException e) {
                        System.err.println("Unable to accept a client");
                    }
                }
            }
        }.start();
    }

    private void newClient(Socket socket){
        //waits for a join-PDU, if any other pdu is sent, the client is not added

        //while we wait for an answer, we continue to listen to others who wants to join
        new Thread() {
            PDU pdu;
            @Override
            public void run() {

                System.out.println("waiting to receive a joinPDU...");
                try {
                    // set timeout
                    socket.setSoTimeout(Constants.WAITTIME);
                    pdu = receiveTCP(socket);
                    System.out.println("got a pdu!");

                    if (pdu instanceof JoinPDU) {
                        socket.setSoTimeout(0);

                        JoinPDU join = (JoinPDU) pdu;
                        boolean canJoin=true;
                        for(ClientInfo c: clients){
                            if (c.getNickName().equals(join.getNickname())){
                                canJoin=false;
                                break;
                            }
                        }
                        if (canJoin) {
                            System.out.println("A socket joined to the server: '"
                                    + socket.getInetAddress() + "' at port: " + socket.getPort() +
                                    " with nickname: '" + join.getNickname() + "'");


                            ClientInfo client = new ClientInfo(join.getNickname(), socket);

                            //says to everyone else that a client had joined and what its name is
                            sendToAll(new UJoinPDU(client.getNickName(), new Date()));
                            clients.add(client);
                            System.out.println("Client: '" + client.getNickName() + "' was added to the list of clients");


                            NicksPDU nicksPDU = new NicksPDU(getClientsNickNames());
                            System.out.println("Sends nicksPDU to client: '" + client.getNickName() + "'");
                            sendTCP(client.getSocket(), nicksPDU);

                            listenToClient(client);
                        }
                        else {
                            System.err.println("Refusing a client to get added because of used nickname");
                            sendTCP(socket, new QuitPDU());
                        }
                    }
                } catch (SocketException s) {
                    // Give up
                    System.out.println("Never received JoinPDU in newClient, timout " + s);
                }

            }
        }.start();
    }

    /**
     * Establishes a inputStream from the socket, needed for receiving tcpPDU:s from the clientSocket
     * this gives you a one-way connection from the client to the server
     * @param client the client
     * @return an InputStream to that socket
     */
    private InputStream getInputStreamFromClient(ClientInfo client){
        try {
            return client.getSocket().getInputStream();
        } catch (IOException e) {
            System.err.println("Could not find the socket to get the inStream from");
            return null;
        }
    }

    private void sendServerMessage(String message) {
        sendToAll(new MessagePDU(message,"",new Date(0)));
    }

    private void sendToAll(PDU pdu) {

        for (ClientInfo client : clients) {
            sendTCP(client.getSocket(),pdu);
        }
    }

    private void sendTCP(Socket socket, byte[] pduArray){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(pduArray,0,pduArray.length);
            bos.writeTo(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Could not send PDU to socket: " + socket);
            e.printStackTrace();
        }
    }

    private void sendTCP(Socket socket,PDU pdu){
        sendTCP(socket, pdu.toByteArray());
    }

    private PDU receiveTCP(Socket socket) throws SocketException  {
        try {
            //waits until whole pdu is received, assuming no soTimer has been set!
            return PDU.fromInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error when reading pdu from inputStream in TCP-connection with client: "+ socket);
            return null;
        }
    }

    private List<String> getClientsNickNames() {
        //returns a list of all the nicknames in the clients-list
        return clients.stream().map(ClientInfo::getNickName).collect(Collectors.toList());
    }


    public void sendUDP(PDU pdu){

        byte[] toSend = pdu.toByteArray();

        DatagramPacket packet = new DatagramPacket(toSend, toSend.length,
                nameServerAddress, nameServerPort);
        try {
            dSocket.send(packet);
        } catch (IOException e) {
            System.err.println("Could not send UDP-packet");
        }

    }

    /**
     * The normal sleep function but with error handling built in
     * @param milliseconds
     */
    private void sleepy(int milliseconds){
        try {
            Thread.sleep(milliseconds);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Listens for Ack and NotReg from the NameServer
     */
    private void listenNameServer() {
        new Thread() {
            public void run() {
                try {
                    //recieve loops forever
                    receiveNameServer();
                } catch (IOException e) {
                    System.err.println("Error when listening to a nameserver\nput up the nameserver if you have not");
                }
            }
        }.start();
    }


    private void receiveNameServer() throws IOException {
        byte[] buffer = new byte[65536];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            //waits until complete package is received
            dSocket.receive(packet);
            PDU inPdu = PDU.fromInputStream(new ByteArrayInputStream(packet.getData()));

            if (inPdu instanceof AckPDU){
                AckPDU ackPDU = (AckPDU) inPdu;
                uniqueID=ackPDU.getUniqueID();
                registered=true;
            }
            else if (inPdu instanceof NotRegPDU){
                registered=false;
            }
            else{
                throw new IOException();
            }
        }
    }

    private void sendNameServer() {
        new Thread() {
            public void run() {
                while (true){
                    //System.out.println("registered to nameServer: "+registered);
                    if (!registered){
                        sendUDP(new RegPDU("Sexy single ladies in your area wanna chat ;)", nameServerPort));
                    }
                    else{
                        sendUDP(new AlivePDU((byte) clients.size(),uniqueID));
                    }
                    //waits 8 seconds before doing it all over again
                    sleepy(Constants.WAITTIME);
                }
            }
        }.start();
    }
}
