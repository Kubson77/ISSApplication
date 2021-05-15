package userapp;

import dao.Dao;
import dao.IDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewWindow implements ActionListener {
    JFrame frame = new JFrame();
    JTextField textFieldDate1 = new JTextField();
    JTextField textFieldDate2 = new JTextField();
    JButton button;
    Dao dao = new Dao();

    NewWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        button = new JButton("Submit");
        button.addActionListener(this);

        textFieldDate1.setPreferredSize(new Dimension(300, 50));
        textFieldDate2.setPreferredSize(new Dimension(300, 50));
        textFieldDate1.setFont(new Font("Consolas", Font.PLAIN, 30));
        textFieldDate2.setFont(new Font("Consolas", Font.PLAIN, 30));
        textFieldDate1.setForeground(new Color(0x00FF00));
        textFieldDate2.setForeground(new Color(0x00FF00));
        textFieldDate1.setBackground(Color.BLACK);
        textFieldDate2.setBackground(Color.BLACK);
        textFieldDate1.setCaretColor(Color.WHITE);
        textFieldDate2.setCaretColor(Color.WHITE);

        frame.add(button);
        frame.add(textFieldDate1);
        frame.add(textFieldDate2);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


            try {
                Date date1 =  formatter.parse(textFieldDate1.getText());
                Date date2 =  formatter.parse(textFieldDate2.getText());

                JOptionPane.showMessageDialog(null, dao.getAvgSpeedByDate
                        (new java.sql.Date(date1.getTime()), new java.sql.Date(date2.getTime())) + " km/h");

            } catch (ParseException parseException) {
                parseException.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


        }
    }
}
