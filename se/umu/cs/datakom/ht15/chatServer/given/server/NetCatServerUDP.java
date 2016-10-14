package umu.cs.datakom.ht15.chatServer.given.server;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class NetCatServerUDP {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        byte[] buffer = new byte[65536];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        DatagramSocket socket = new DatagramSocket(port);

        new Thread() {
            public void run() {
                try {
                    write(buffer, port);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        read(packet, socket);
    }

    private static void read(DatagramPacket packet, DatagramSocket socket) throws IOException {
        while (true) {
            socket.receive(packet);
            InetAddress sentFrom = packet.getAddress();
            int sentPort = packet.getPort();
            String out = new String("Sent from: " + sentFrom.toString()
                                    + ", with port: " + sentPort);
            System.out.println(new String(packet.getData()).trim());
        }
    }

    private static void write(byte[] buffer, int port) throws SocketException {
        InetAddress local = null;
        DatagramSocket sc = new DatagramSocket();
        try {
            local = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, local,port);

        Scanner in =
                new Scanner(System.in);

        String userInput;
        while (true) {
            userInput = in.nextLine();
            packet.setData(userInput.getBytes());
            try {
                sc.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}