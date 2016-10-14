package umu.cs.datakom.ht15.chatServer.given.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * File:        ChatPanel.java
 * Author:      Niklas Fries
 * Contact:     niklasf@cs.umu.se
 * Date:        2015-08-21
 */

/**
 * JPanel interface for a ChatModule instance. Allows sending messages,
 * changing nickname, listing members, has a text
 * area for messages and a button for leaving.
 */
public class ChatPanel extends JPanel {

    private JTextArea receivedMessages = new JTextArea();
    private JTextField messageInputField = new JTextField();
    private JButton sendButton = new JButton("Send");
    private DefaultListModel<String> memberListModel = new DefaultListModel<>();
    private JTextField nickField;
    private JButton changeNickButton;
    private JButton closeButton;

    public ChatPanel() {

        setLayout(new BorderLayout());
        receivedMessages.setEditable(false);

        final JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(new JScrollPane(receivedMessages), BorderLayout.CENTER);

        add(inputPanel, BorderLayout.SOUTH);

        receivedMessages.setLineWrap(true);

        messageInputField.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            sendButton.doClick();
                        }
                    }
                });

        add(getSideBar(), BorderLayout.EAST);
        messageInputField.requestFocus();
    }

    private JPanel getSideBar() {

        JPanel sideBar = new JPanel(new BorderLayout());
        sideBar.setPreferredSize(new Dimension(300, 0));

        JList<String> memberList = new JList<>(memberListModel);
        sideBar.add(new JScrollPane(memberList), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));

        sideBar.add(buttonsPanel, BorderLayout.SOUTH);

        JPanel nickPanel = new JPanel(new BorderLayout());
        changeNickButton = new JButton("Change nickname");
        nickField = new JTextField();
        nickPanel.add(nickField, BorderLayout.CENTER);
        nickPanel.add(changeNickButton, BorderLayout.EAST);

        buttonsPanel.add(nickPanel);

        JPanel closePanel = new JPanel();
        closeButton = new JButton("Close");
        closePanel.add(closeButton);

        buttonsPanel.add(closePanel, BorderLayout.SOUTH);

        sideBar.add(buttonsPanel, BorderLayout.SOUTH);

        return sideBar;
    }

    public void addSendListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }

    public String getMessage() {
        return messageInputField.getText();
    }

    public void addNickname(String nickname) {
        memberListModel.addElement(nickname);
    }

    public void removeNickname(String nickname) {
        memberListModel.removeElement(nickname);
    }

    public void appendMessage(String message) {
        receivedMessages.append(message);
    }

    public void addChangeNickListener(ActionListener listener) {
        changeNickButton.addActionListener(listener);
    }

    public String getNewNickname() {
        return nickField.getText();
    }

    public void addCloseTabListener(ActionListener listener) {
        closeButton.addActionListener(listener);
    }

    public void clearMessage() {
        messageInputField.setText("");
    }
}
