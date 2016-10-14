package umu.cs.datakom.ht15.chatServer.given.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * File:        NameServerPanel.java
 * Author:      Niklas Fries
 * Contact:     niklasf@cs.umu.se
 * Date:        2015-08-21
 */

/**
 * JPanel providing an interface for an instance of NameServerModule.
 * Allows selecting name server address and port, requesting server list,
 * and entering addresses and port numbers to chat servers and joining.
 */
public class NameServerPanel extends JPanel {

    private final DefaultTableModel serverTableModel = new DefaultTableModel(
            new Object[][]{},
            new Object[]{"IP", "Port", "Topic", "Clients"}
    );

    private JTextField nsAddressField;
    private JTextField nsPortField;
    private JButton getServersButton;

    private JTextField csAddressField;
    private JTextField csPortField;
    private JButton joinButton;

    private JTextField nickField;

    public NameServerPanel() {
        setLayout(new BorderLayout());

        JTable serverTable = new JTable(serverTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serverTable.getSelectionModel().addListSelectionListener(
                listSelectionEvent -> {
                    int row = serverTable.getSelectedRow();
                    if (row > -1 && row < serverTable.getRowCount()) {
                        Object[] rowData =
                                new Object[serverTable.getColumnCount()];
                        for (int i = 0; i < rowData.length; i++) {
                            rowData[i] = serverTable.getValueAt(row, i);
                        }
                        csAddressField.setText(
                                rowData[0].toString().replace("/", ""));
                        csPortField.setText(rowData[1].toString());
                    }
                });

        add(new JScrollPane(serverTable), BorderLayout.CENTER);

        add(getSideBar(), BorderLayout.EAST);
    }

    private JPanel getSideBar() {

        /** One grid for name servers and one for joining */
        JPanel sideBar = new JPanel(new GridLayout(2, 1));
        sideBar.setPreferredSize(new Dimension(200, 0));

        /** Name server panel */
        JPanel nsPanel = new JPanel();
        sideBar.add(nsPanel);
        nsPanel.setLayout(new BoxLayout(nsPanel, BoxLayout.Y_AXIS));

        /** Text fields and button for getting server list */
        nsAddressField = new JTextField("itchy.cs.umu.se");
        nsPanel.add(
                panelWithFieldAndLabel(
                        nsAddressField,
                        "Name server address"));
        nsPortField = new JTextField("1337");
        nsPanel.add(panelWithFieldAndLabel(nsPortField, "Name server port"));
        getServersButton = new JButton("Request server List");
        nsPanel.add(getButtonPanel(getServersButton));

        /** Chat server panel. */
        JPanel csPanel = new JPanel();
        sideBar.add(csPanel);
        csPanel.setLayout(new BoxLayout(csPanel, BoxLayout.Y_AXIS));

        /** Text fields and button for joining chat */
        csAddressField = new JTextField();
        csPanel.add(
                panelWithFieldAndLabel(
                        csAddressField,
                        "Chat server address"));
        csPortField = new JTextField();
        csPanel.add(panelWithFieldAndLabel(csPortField, "Chat server port"));
        nickField = new JTextField();
        csPanel.add(panelWithFieldAndLabel(nickField, "Nickname"));
        joinButton = new JButton("Join chat");
        csPanel.add(getButtonPanel(joinButton));

        return sideBar;
    }

    private JPanel getButtonPanel(JButton joinButton) {
        JPanel joinPanel = new JPanel();
        joinPanel.add(joinButton);
        return joinPanel;
    }

    private JComponent panelWithFieldAndLabel(JTextField field, String label) {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel theLabel = new JLabel(label);
        panel.add(theLabel);
        panel.add(field);
        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        2 * (int) field.getPreferredSize().getHeight()));
        return panel;
    }

    public void addRequestServerListListener(ActionListener listener) {
        getServersButton.addActionListener(listener);
    }

    public void addJoinChatListener(ActionListener listener) {
        joinButton.addActionListener(listener);
    }

    public String getNameServerAddress() {
        return nsAddressField.getText();
    }

    public int getNameServerPort() throws NumberFormatException {
        return Integer.parseInt(nsPortField.getText());
    }

    public String getChatServerAddress() {
        return csAddressField.getText();
    }

    public int getChatServerPort() throws NumberFormatException {
        return Integer.parseInt(csPortField.getText());
    }

    public String getNickname() {
        return nickField.getText();
    }

    public void setServerTable(Collection<String[]> servers) {
        serverTableModel.setRowCount(0);
        servers.forEach(serverTableModel::addRow);
    }
}
