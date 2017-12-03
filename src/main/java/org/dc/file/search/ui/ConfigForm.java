package org.dc.file.search.ui;

import org.apache.commons.lang.RandomStringUtils;
import org.dc.file.search.MessageUtils;
import org.dc.file.search.Peer;
import org.dc.file.search.Store;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ConfigForm {
    private JTextField ipTF;
    private JTextField portTF;
    private JButton saveButton;
    private JPanel panel1;

    public static void show() {
        JFrame frame = new JFrame("ConfigForm");
        frame.setContentPane(new ConfigForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public ConfigForm() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save IP and Port
                Store store = Store.getInstance();
                store.setServerIp(ipTF.getText());
                store.setServerPort(Integer.parseInt(portTF.getText()));

                // Register the peer
                String uuid = RandomStringUtils.randomAlphanumeric(8);
                Peer localPeer = null;
                try {
                    localPeer = MessageUtils.init(uuid);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                MessageUtils.sendTCPMessage(store.getServerIp(),
                        store.getServerPort(),
                        "REG " + localPeer.getIp() + " " + localPeer.getPort() + " " + uuid);
            }
        });
    }
}
