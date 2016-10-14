package umu.cs.datakom.ht15.chatServer.given.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class NetCatUDPSocket {
    /**
     * SYNOPSIS: NetCatServerTCP port
     */
    public static void main(String[] args) throws IOException {

        /** Port for the server to listen to. */
        int port = Integer.parseInt(args[1]);
        byte[] buffer = new byte[65536];
        InetAddress host = InetAddress.getByName(args[0]);
        DatagramSocket sc = new DatagramSocket();
        sc.connect(host, port);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, port);

        //PrintStream out =
        //        new PrintStream(packet., true);
        Scanner in =
                new Scanner(System.in);

        String userInput;
        while (true) {
            userInput = in.nextLine();
            packet.setData(userInput.getBytes());
            sc.send(packet);

        }
    }
}
