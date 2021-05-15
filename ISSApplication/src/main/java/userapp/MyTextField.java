package userapp;

import javax.swing.*;
import java.awt.*;


public class MyTextField extends JTextField {
    JFrame frame = new JFrame();

    MyTextField(){
        JTextField textField = new JTextField();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(200,40));
        frame.add(textField);
        setVisible(true);

    }
}
