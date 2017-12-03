package org.dc.file.search.ui;

import org.dc.file.search.Store;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyMoviesList {
    private JButton button1;
    private JPanel panel1;

    public static void show() {
        JFrame frame = new JFrame("MyMoviesList");
        frame.setContentPane(new MyMoviesList().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public MyMoviesList() {
        // Load movies list
        Store store = Store.getInstance();
        store.displayFilesList();
    }
}
