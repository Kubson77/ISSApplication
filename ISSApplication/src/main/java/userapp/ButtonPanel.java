package userapp;

import dao.Dao;
import dao.IDao;
import model.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

class ButtonPanel extends JPanel implements ActionListener {
    public static final int HEIGHT = 400;
    public static final int WIDTH = 1500;
    IDao dao = new Dao();
    private JButton getSpeedButton;
    private JButton astronautsNum;
    private JButton locationButton;
    private JButton astronautsList;
    private JButton refreshAstronautsList;
    private JButton speedInPeriod;




    public ButtonPanel() {
        locationButton = new JButton("Last Location");
        getSpeedButton = new JButton("Speed of ISS");
        astronautsNum = new JButton("Astronauts Number");
        astronautsList = new JButton("Astronauts List");
        refreshAstronautsList = new JButton("Refresh Astronaut Lists");
        speedInPeriod = new JButton("Speed in Period of Time");

        locationButton.addActionListener(this);
        getSpeedButton.addActionListener(this);
        astronautsNum.addActionListener(this);
        astronautsList.addActionListener(this);
        refreshAstronautsList.addActionListener(this);
        speedInPeriod.addActionListener(this);

        setLayout(new GridLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        add(locationButton);
        add(getSpeedButton);
        add(astronautsNum);
        add(astronautsList);
        add(refreshAstronautsList);
        add(speedInPeriod);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == locationButton) {
            String msg = String.format("Latitude: " + dao.getLatitude() + " Longitude: "
                    + dao.getLongitude());
            JOptionPane.showMessageDialog(null, msg);
        } else if (source == getSpeedButton) {
            String speedMsg = String.format("Speed: " + dao.getIssSpeed() + " km/h");
            JOptionPane.showMessageDialog(null, speedMsg);
        } else if (source == astronautsNum) {
            String astronautsMsg = String.format("Number of Astronauts: " + dao.getHowManyPeopleInIss());
            dao.getAstronauts();
            JOptionPane.showMessageDialog(null, astronautsMsg);
        } else if (source == astronautsList) {
            List<Person> astronauts = dao.getAstronauts();
            StringBuilder astronautsMsg = new StringBuilder();

            for (Person person : astronauts) {
                astronautsMsg.append(person.toString());
                astronautsMsg.append("\n");
            }

            JOptionPane.showMessageDialog(null, astronautsMsg);
        } else if (source == refreshAstronautsList) {
            dao.refreshListOfAstronauts();
            JOptionPane.showMessageDialog(null, "OK");
        }else if (source == speedInPeriod){
            JOptionPane.showMessageDialog(null, "Provide date from and date to");
            NewWindow newWindow = new NewWindow();
        }

    }
}
