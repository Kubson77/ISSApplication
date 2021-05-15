package userapp;

import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {
    public MyFrame() {
        super("ISS Tracker");

        JPanel buttonPanel = new ButtonPanel();
        add(buttonPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MyTextField textField = new MyTextField();
        pack();
        setVisible(true);

    }
}

