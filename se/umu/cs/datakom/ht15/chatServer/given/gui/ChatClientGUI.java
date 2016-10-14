package umu.cs.datakom.ht15.chatServer.given.gui;

import javax.swing.*;

/**
 * file: ChatClientGUI.java
 * @author c12oor niklasf
 * date: 2014-10-23
 *
 * GUI for the chat client. Contains a tab pane with a tab for the name server
 * interface and a tab for each chat room.
 */
public class ChatClientGUI extends JFrame {

    private final JTabbedPane tabs = new JTabbedPane();
    private final NameServerPanel nameServerPanel = new NameServerPanel();

    public ChatClientGUI() {
        super("Chattring chatprogram!");
        setSize(800, 600);
        add(tabs);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        tabs.add(nameServerPanel, "Name Server");
        setVisible(true);
    }

    public NameServerPanel getNameServerPanel() {
        return nameServerPanel;
    }

    public JTabbedPane getTabs() {
        return tabs;
    }
}
