package umu.cs.datakom.ht15.chatServer.given.server;

import umu.cs.datakom.ht15.chatServer.given.gui.ChatClientGUI;
import umu.cs.datakom.ht15.chatServer.given.gui.ChatModule;
import umu.cs.datakom.ht15.chatServer.given.server.Constants;
import umu.cs.datakom.ht15.chatServer.given.server.Server;

import java.io.IOException;
import java.util.Date;

/**
 * Created by marccoquand on 28/09/15.
 */
public class Main {

    public static void main(String[] args) {
        //Server s = new Server("itchy.cs.umu.se", (short) 1337);
        Server s1 = new Server(args[0],Short.parseShort(args[1]));
        new ChatModule(args[0], Short.parseShort(args[1]),"Bot1");
        new ChatModule(args[0], Short.parseShort(args[1]),"Bot2");
        new ChatModule(args[0], Short.parseShort(args[1]),"Bot3");


    }
}