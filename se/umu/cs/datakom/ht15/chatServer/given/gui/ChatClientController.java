package umu.cs.datakom.ht15.chatServer.given.gui;

import javax.swing.*;

/**
 * File:        ChatClientController.java
 * Author:      Niklas Fries
 * Contact:     niklasf@cs.umu.se
 * Date:        2015-08-21
 */

/**
 * Controller class for GUI src at 5DV166/5DV167. This class should not
 * be necessary to modify to complete the assignment.
 */
public class ChatClientController {

    private final ChatClientGUI gui;

    public ChatClientController() {

        NameServerModule nameServerModule = new NameServerModule();

        gui = new ChatClientGUI();
        gui.getNameServerPanel().addRequestServerListListener(
                e -> {
                    try {
                        NameServerPanel nsPanel = gui.getNameServerPanel();
                        String address = nsPanel.getNameServerAddress();
                        int port = nsPanel.getNameServerPort();
                        nameServerModule.getServerList(address, port);
                    } catch (NumberFormatException nfe) {
                        JOptionPane
                                .showMessageDialog(gui, "Invalid port number");
                    }
                });

        nameServerModule.addListener(
                s -> SwingUtilities.invokeLater(
                        () -> gui.getNameServerPanel().setServerTable(s)));

        gui.getNameServerPanel().addJoinChatListener(
                event -> {
                    NameServerPanel nsPanel = gui.getNameServerPanel();
                    if (nsPanel.getNickname().length() < 1) {
                        JOptionPane.showMessageDialog(
                                gui,
                                "Please enter a nickname");
                        return;
                    }
                    try {
                        addChatTab(
                                nsPanel.getChatServerAddress(),
                                nsPanel.getChatServerPort(),
                                nsPanel.getNickname()
                        );
                    } catch (NumberFormatException e) {
                        JOptionPane
                                .showMessageDialog(gui, "Invalid port number");
                    }
                });
    }

    private void addChatTab(
            String address,
            int port,
            String nickname) {

        ChatPanel tab = new ChatPanel();
        ChatModule chatModule =
                new ChatModule(address, port, nickname);
        tab.addSendListener(e -> {
                    chatModule.sendMessage(tab.getMessage());
                    tab.clearMessage();
                });
        tab.addChangeNickListener(
                e -> chatModule.changeNick(tab.getNewNickname()));
        tab.addCloseTabListener(
                e -> {
                    gui.getTabs().remove(tab);
                    gui.getTabs().setSelectedIndex(0);
                    chatModule.leave();
                });
        chatModule.addJoinListener(
                n -> SwingUtilities.invokeLater(() -> tab.addNickname(n)));
        chatModule.addLeaveListener(
                n -> SwingUtilities.invokeLater(() -> tab.removeNickname(n)));
        chatModule.addMessageListener(
                m -> SwingUtilities.invokeLater(() -> tab.appendMessage(m)));
        gui.getTabs().add(tab, address + ":" + port);
        gui.getTabs().setSelectedComponent(tab);
    }

    public static void main(String[] args) {
        new ChatClientController();
    }
}
