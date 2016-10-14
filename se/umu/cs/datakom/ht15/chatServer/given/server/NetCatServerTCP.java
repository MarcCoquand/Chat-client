package umu.cs.datakom.ht15.chatServer.given.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetCatServerTCP {

    private static List<Socket> sockets = new CopyOnWriteArrayList<Socket>();

    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket = new ServerSocket(port);

        new Thread() {
            public void run() {
                while (true) {
                    sockets.add(gatherMessages(serverSocket));
                }
            }
        }.start();

        new Thread() {
            public void run() {
                PrintWriter out;
                List<Socket> toRemove = new ArrayList<>();
                while (true) {
                    for (Socket s : sockets) {
                        try {
                            out = new PrintWriter(s.getOutputStream(), true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (s.getInputStream().read() == -1) {
                                System.out.println("A brave man was lost");
                                System.out.flush();
                                toRemove.add(s);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        for (Socket removee : toRemove) {
                            sockets.remove(removee);
                        }
                        toRemove.clear();
                    }
                }
            }
        }.start();

            //for writing to the others
            new Thread() {
                @Override
                public void run () {
                    Scanner in =
                            new Scanner(System.in);
                    String userInput;
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        userInput = in.nextLine();
                        for (Socket s : sockets) {
                            write(s, userInput);
                        }
                    }
                }
            }
            .start();
        }

    private static Socket gatherMessages(ServerSocket serverSocket) {
        Socket s = null;
        try {
            s = serverSocket.accept();
            final Socket finalS = s;
            new Thread() {
                public void run() {
                    try {
                        sayToEveryone(finalS.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    //om den får emot en rad så skickar den den till alla
    private static void sayToEveryone(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        PrintWriter socketOutPut=null;
        String whatToSay = null;
        // Put everything in threadsafe queue
        /* Send it to everyone */
        while (scanner.hasNextLine()) {

            whatToSay=scanner.nextLine();

            for(Socket s: sockets) {
                try {
                    socketOutPut = new PrintWriter(s.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socketOutPut.println(whatToSay);
            }

        }
    }

    private static boolean write(Socket socket, String s) {
        PrintStream out = null;
        try {
            out = new PrintStream(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out == null) {
            return false;
        }
        else {
            out.println(s);
            return true;
        }
    }
}