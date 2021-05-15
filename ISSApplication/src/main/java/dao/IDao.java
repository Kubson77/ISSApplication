package dao;

import model.ISSPosition;
import model.Person;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface IDao {

    String getLatitude();

    String getLongitude();

    int getIssSpeed();

    int getAvgSpeedByDate(Date from, Date to) throws IOException, SQLException;

    void refreshListOfAstronauts();

    int getHowManyPeopleInIss();

    List<Person> getAstronauts();
}
