package dao;

import model.ISSPosition;
import model.Person;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static java.lang.Math.PI;

public class Dao implements IDao {
    private static final Logger log;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
        log = Logger.getLogger(Dao.class.getName());
    }

    private final String queryGetLastCoordinates = "SELECT TOP 1 * FROM iss_position ORDER BY id DESC";
    private final String queryGetOneLastCoordinates = "SELECT TOP 1 * FROM (SELECT TOP 2 * FROM iss_position ORDER BY id DESC) iss_position ORDER BY id";
    private final String queryHowManyAstronauts = "SELECT COUNT(id) AS id FROM iss_astronauts";
    private final String queryGetAstros = "SELECT * FROM iss_astronauts ORDER BY id";
    private final String queryAstrosTable = "SELECT TOP 1 * FROM iss_astronauts ORDER BY id";
    private final String dropAstonautsTable = "DROP table iss_astronauts";
    private final String createAstonautsTable = "CREATE TABLE iss_astronauts(\n" +
            "id INT IDENTITY,\n" +
            "first_name VARCHAR(50) NOT NULL,\n" +
            "last_name VARCHAR(50) NOT NULL,\n" +
            "PRIMARY KEY(id)\n" +
            ");\n";
    private URL urlPosition;
    private URL urlAstronauts;
    private Connection connection;

    {
        try {
            urlPosition = new URL("http://api.open-notify.org/iss-now.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    {
        try {
            urlAstronauts = new URL("http://api.open-notify.org/astros.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public List<ISSPosition> getAllData() throws IOException, SQLException {
        List<ISSPosition> issPositionList = new ArrayList<>();
        openConnection();
        try {
            String query = "SELECT * FROM iss_position";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                issPositionList.add(getIssPositionFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return issPositionList;
    }

    @Override
    public String getLatitude() {
        // todo
        try {
            return getLastIssCoordinates().getLatitude();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ISSPosition getLastIssCoordinates() throws IOException, SQLException {

        ISSPosition issPosition = new ISSPosition();
        openConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryGetLastCoordinates);
            while (resultSet.next()) {
                issPosition = getIssPositionFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        closeConnection();
        return issPosition;
    }


    @Override
    public String getLongitude() {
        //todo
        try {
            return getLastIssCoordinates().getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getIssSpeed() {
        //todo
        try {
            return getLastIssCoordinates().getSpeed();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 28000;
    }

    @Override
    public int getAvgSpeedByDate(Date from, Date to) throws IOException, SQLException {
        String query = String.format("SELECT AVG(speed) FROM iss_position WHERE date BETWEEN '%tF' AND '%tF'",
                from, to);
        ISSPosition position = new ISSPosition();
        int result = 0;
        openConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            result = result + resultSet.getInt(1);
        }
        closeConnection();
        return result;
    }


    @Override
    public void refreshListOfAstronauts() {
        try {
            openConnection();

            update(dropAstonautsTable);
            update(createAstonautsTable);
            addIssAstronauts(collectAstronautsData());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public int getHowManyPeopleInIss() {
        int number = -1;
        try {
            openConnection();

            PreparedStatement readStatement = connection.prepareStatement(queryHowManyAstronauts);
            ResultSet resultSet = readStatement.executeQuery();
            if (resultSet.next()) {
                number = Integer.parseInt(resultSet.getString("id"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return number;
    }

    @Override
    public List<Person> getAstronauts() {
        List<Person> astronauts = new ArrayList<>();

        try {
            openConnection();

            if (!checkAstrosTable()) {
                update(dropAstonautsTable);
                update(createAstonautsTable);
                addIssAstronauts(collectAstronautsData());
            }
            astronauts = getAstronautsFromTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return astronauts;
    }

    private void addIssAstronauts(List<Person> astronauts) {

        for (Person person : astronauts) {
            String insertIssAstronauts = String.format("INSERT INTO iss_astronauts (first_name, last_name) VALUES('%s', '%s')",
                    person.getFirstName(), person.getLastName());

            update(insertIssAstronauts);
        }
    }

    private int update(String input) {

        int result = 1;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeUpdate(input);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    private List<Person> getAstronautsFromTable() {

        List<Person> astronauts = new ArrayList<>();
        try {
            PreparedStatement readStatement = connection.prepareStatement(queryGetAstros);
            ResultSet resultSet = readStatement.executeQuery();
            while (resultSet.next()) {
                Person person = new Person();
                person.setAstronautId(Integer.valueOf(resultSet.getString("id")));
                person.setFirstName(resultSet.getString("first_name"));
                person.setLastName(resultSet.getString("last_name"));
                astronauts.add(person);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return astronauts;
    }

    private List<Person> collectAstronautsData() {

        URLConnection urlcon = null;
        try {
            urlcon = urlAstronauts.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream input = null;
        try {
            input = urlcon.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));

        JSONObject astros = null;
        try {
            astros = (JSONObject) new JSONParser().parse(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray people = (JSONArray) astros.get("people");
        List<Person> astronauts = new ArrayList<>();
        for (Object o : people) {
            Person person = new Person();
            JSONObject astronaut = (JSONObject) o;
            String fullName = (String) astronaut.get("name");
            String[] splitName = fullName.split(" ");
            if (splitName.length > 1) {
                person.setFirstName(splitName[0]);
                StringBuilder lastName = new StringBuilder();
                for (int i = 1; i < splitName.length; i++) {
                    lastName.append(splitName[i]);
                    if (splitName.length - 1 > i) {
                        lastName.append(" ");
                    }
                }
                person.setLastName(lastName.toString());
                astronauts.add(person);
            }
        }

        return astronauts;
    }

    private boolean checkAstrosTable() {
        boolean check = true;

        try {

            PreparedStatement readStatement = connection.prepareStatement(queryAstrosTable);
            ResultSet resultSet = readStatement.executeQuery();
            if (!resultSet.next()) {
                log.info("There is no data in the database!");
                check = false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return check;
    }

    private void openConnection() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(Dao.class.getClassLoader().getResourceAsStream("application.properties"));
        connection = DriverManager.getConnection(properties.getProperty("url"), properties);
    }

    private void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private ISSPosition getIssPositionFromResultSet(ResultSet resultSet) throws SQLException {
        ISSPosition issPosition = new ISSPosition();
        issPosition.setLatitude(resultSet.getString("latitude"));
        issPosition.setLongitude(resultSet.getString("longitude"));
        issPosition.setUnixTime(resultSet.getLong("timestamp"));
        issPosition.setDate(resultSet.getDate("date"));
        issPosition.setSpeed(resultSet.getInt("speed"));
        return issPosition;
    }

}
