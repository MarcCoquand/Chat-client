package umu.cs.datakom.ht15.chatServer.given.gui;

import umu.cs.datakom.ht15.chatServer.given.pdu.PDU;
import umu.cs.datakom.ht15.chatServer.given.pdu.pduTypes.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.sleep;

/**
 * Model for chat module in clients.
 * <br/>
 * TODO replace code where indicated.
 */
public class ChatModule {

    private final List<Listener<String>> joinListeners = new ArrayList<>();
    private final List<Listener<String>> leaveListeners = new ArrayList<>();
    private final List<Listener<String>> messageListeners = new ArrayList<>();
    private InputStream inputStream; // to get pdu:s from chatServer
    private OutputStream outStream; // to send pdu:s to chatServer
    private String nickname;
    private Socket socket;
    private boolean registered=true;
    private List<String> nicknames = new CopyOnWriteArrayList<>();
    /**
     * Creates a new chat module and attempts to join the server at the
     * specified port and address with the src nickname.
     *
     * @param address The address of the server.
     * @param port The port of the server.
     * @param nickname The desired nickname.
     */
    public ChatModule(String address, int port, String nickname) {
        // TCP Connect to server
        // Send Join PDU
        // Receive Nicks PDU
        // Invalid PDU should result in quit
        this.nickname = nickname;
        System.err.println(
                "Attempting to join server at " + address + ":" + port +
                        " with nickname '" + nickname + "'");

        try {
            socket = new Socket(address, port);
            inputStream = socket.getInputStream();
            outStream = socket.getOutputStream();

            sendToServer(new JoinPDU(nickname));

        } catch (IOException e) {
            System.err.println("Could not establish a connection to the chatServer from the client");
        }

        listenToChatServer();
    }

    private void listenToChatServer() {
        new Thread() {
            public void run() {
                while (registered) {
                    try {
                        PDU pdu = PDU.fromInputStream(socket.getInputStream());

                        if (pdu instanceof MessagePDU) {
                            MessagePDU mPDU = (MessagePDU) pdu;
                            //Update chat
                            notifyMessageListeners(mPDU.toString());
                            System.out.println("Message from server" + mPDU);
                        }
                        else if (pdu instanceof QuitPDU) {
                            leave();
                        }
                        else if (pdu instanceof UJoinPDU){
                            notifyJoinListeners(((UJoinPDU) pdu).getNickname());
                            nicknames.add(((UJoinPDU) pdu).getNickname());
                        }
                        else if (pdu instanceof NicksPDU) {
                            System.out.println("Got a nicksPDU from the server");
                            registered = true;

                            for (String s : ((NicksPDU) pdu).getNicknames()) {
                                System.err.println(s);
                                notifyJoinListeners(s);
                                nicknames.add(s);
                            }
                        }
                        else if(pdu instanceof ULeavePDU) {
                            ULeavePDU uLeavePDU = (ULeavePDU) pdu;

                            if (nicknames.contains(uLeavePDU.getNickname()))
                                nicknames.remove(uLeavePDU.getNickname());
                            System.err.println("    "+uLeavePDU.getNickname()+" should disappear from the list");

                            notifyLeaveListeners(uLeavePDU.getNickname());

                        }
                        else if(pdu instanceof UCNickPDU) {
                            UCNickPDU ucNickPDU = (UCNickPDU)pdu;

                            if (nicknames.contains(ucNickPDU.getOldNick())){
                                nicknames.remove(ucNickPDU.getOldNick());
                            }
                            nicknames.add(ucNickPDU.getNewNick());

                            if (ucNickPDU.getOldNick()==nickname){
                                nickname=ucNickPDU.getNewNick();
                            }

                            notifyLeaveListeners(ucNickPDU.getOldNick());
                            notifyJoinListeners(ucNickPDU.getNewNick());
                        }
                        else{
                            printPDUByteArray("Unknown pdu from server",pdu.toByteArray());
                            leave();
                        }

                    } catch (IOException e) {
                        System.out.println("Invalid op-code from chatserver to client");
                        sendToServer(new QuitPDU());
                    }
                }
            }
        }.start();
    }

    public static void printPDUByteArray(String s,byte[] hej) {
        System.err.println(s);
        System.out.flush();
        for (int i = 0; i < hej.length; i++) {

            System.err.printf("%5d",hej[i]);
            if ((i+1)%4==0 && i!=0){
                System.err.println();
            }
            else System.err.print(",");
        }
        System.out.println();
    }

    private void sendToServer(PDU pdu){
        byte[] toSend = pdu.toByteArray();
        ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
        bOutStream.write(toSend, 0, toSend.length);
        try {
            bOutStream.writeTo(outStream);
        } catch (IOException e) {
            System.err.println("Could not send pdu to server because of IOexception in writeTo");
        }
    }

    /**
     * Call this to send a message to the server.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        System.err.println("Sending message: \"" + message + "\"");
        sendToServer(new MessagePDU(message));
    }

    /**
     * Call this to change nickname.
     *
     * @param newNick The desired nickname.
     */
    public void changeNick(String newNick) {
        System.err.println("Changing nickname to: \"" + newNick + "\"");
        sendToServer(new ChNickPDU(newNick));
    }

    /**
     * Call this to leave the chat.
     */
    public void leave() {
        System.err.println("Leaving chat");
        sendToServer(new QuitPDU());
        notifyLeaveListeners(nickname);
        registered = false;
    }

    /**
     * Call this whenever a member joins the chat.
     *
     * @param nickname The nickname of the new member.
     */
    private void notifyJoinListeners(String nickname) {
        joinListeners.forEach(l -> l.update(nickname));
    }

    /**
     * Call this whenever a member leaves the chat.
     *
     * @param nickname The nickname of the leaving member.
     */
    private void notifyLeaveListeners(String nickname) {
        leaveListeners.forEach(l -> l.update(nickname));
    }

    /**
     * Call this whenever a message is received.
     *
     * @param message The message.
     */
    private void notifyMessageListeners(String message) {
        messageListeners.forEach(l -> l.update(message));
    }

    /**
     * Adds a listener to be notified when a new member joins the chat.
     *
     * @param listener The listener whose update method will be called.
     */
    public void addJoinListener(Listener<String> listener) {
        joinListeners.add(listener);
    }

    /**
     * Adds a listener to be notified when a member leaves the chat.
     *
     * @param listener The listener whose update method will be called.
     */
    public void addLeaveListener(Listener<String> listener) {
        leaveListeners.add(listener);
    }

    /**
     * Adds a listener to be notified when a message is received.
     *
     * @param listener The listener whose update method will be called.
     */
    public void addMessageListener(Listener<String> listener) {
        messageListeners.add(listener);
    }
}
