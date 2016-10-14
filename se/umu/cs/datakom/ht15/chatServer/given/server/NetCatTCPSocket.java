package umu.cs.datakom.ht15.chatServer.given.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by marccoquand on 04/09/15.
 */
public class NetCatTCPSocket {
    /**
     * SYNOPSIS: NetCatServerTCP port
     */
    public static void main(String[] args) throws IOException {

        /** Port for the server to listen to. */
        String address = args[0];
        int port = Integer.parseInt(args[1]);

        Socket socket = new Socket(address, port);

        InputStream inputStream = socket.getInputStream();
        Scanner scanner = new Scanner(inputStream);

        new Thread() {
            public void run()
            {
                while (scanner.hasNextLine()) {
                    System.out.println(scanner.nextLine());
                }
            }
        }.start();

        PrintStream out =
                new PrintStream(socket.getOutputStream(), true);
        Scanner in =
                new Scanner(System.in);
        String userInput;
        while (true) {
            userInput = in.nextLine();
            out.println(userInput);
        }
    }

}
