package umu.cs.datakom.ht15.chatServer.given.server;

import umu.cs.datakom.ht15.chatServer.given.gui.Listener;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Client {

    private final Collection<Listener<String>> listeners =
            new ArrayList<>();

    private final Socket socket;

    public Client(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
    }

    public void addTCPMessageListener(Listener<String> listener) {
        listeners.add(listener);
    }

    public void addTCPMessageSender() {

    }

    /*
    public void sendMessage(String msg) {
    }
    */


    public void stopThread() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
