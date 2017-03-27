package DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DataService.PlayerService;
import Layout.LayoutInit;
import Layout.LeagueView;


public class DatabaseConnection
{
    private static String dbURL = "jdbc:derby:./DB;create=true;";
    private Connection conn;
    private DatabaseUpdateContent duc;
    private DatabaseUpdateView duv;
    private LayoutInit controller;

    public DatabaseConnection(LayoutInit controller)
    {
        this.controller = controller;
    }

    public synchronized void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // load the driver
            conn = DriverManager.getConnection(dbURL); // make Derby JDBC connection
            duc = new DatabaseUpdateContent(conn);
            duv = new DatabaseUpdateView(conn, controller);
        }
        catch (Exception e)
        {
            controller.log("Cannot connect to database. Reason: " + e.getMessage(), 2);
        }
    }

    public synchronized void recreateConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(dbURL); // make Derby JDBC connection
            duc = new DatabaseUpdateContent(conn);
            duv = new DatabaseUpdateView(conn, controller);
        }

        catch(Exception e)
        {
            controller.log("Cannot connect to database. Reason: " + e.getMessage(),2);
        }

    }

    public synchronized boolean updatePlayer(PlayerService player)
    {
        String firstName = player.getFirstName();
        String lastName = player.getLastName();
        try
        {
            int ID = player.getID();
            Date birthdate;
            if(!(player.getDate() == null))
                birthdate =  new java.sql.Date(player.getDate().getTime());
            else
                birthdate = null;
            String league = player.getTableName().toUpperCase();
            String team = player.getTeamName();
            int apps = player.getApps();
            int firstSquad = player.getFirstSquad();
            int minutes = player.getMinutes();
            int goals = player.getGoals();
            int yellowCards = player.getYellowCards();
            int redCards = player.getRedCards();
            System.out.println(Thread.currentThread().getId() + " " + ID + " " + firstName + " " + lastName);
            duc.updatePlayersTable(ID, firstName, lastName, birthdate);
            duc.updateLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
        }
        catch (SQLIntegrityConstraintViolationException sqlE)
        {
            controller.log("Cannot insert footballer " + firstName + " " + lastName + " to database due to SQLIntegrityConstraintViolationException",2);
            return true; // to avoid inserting bad data
        }
        catch (SQLException e)
        {
            /*while (e != null)
            {
                System.out.println("Thread: " + Thread.currentThread().getId() + "\n----- SQLException -----");
                System.out.println("  SQLState:   " + e.getSQLState());
                System.out.println("  Error Code: " + e.getErrorCode());
                System.out.println("  Message:    " + e.getMessage());
                e.printStackTrace(System.out);
                e = e.getNextException();
            }
            // for stack dumps, refer to derby.log or add
            //e.printStackTrace(System.out); above*/

            controller.log("Cannot insert footballer " + firstName + " " + lastName + " to database. Reason: " + e.getMessage(),2);
            return false;
        }
        return true;
    }

    public synchronized void updateView(LeagueView view, String leagueName, String orderBy, boolean desc)
    {
        duv.updateView(view, leagueName, orderBy, desc);
    }

    public List<String> getTablesNames()
    {
        try
        {
            DatabaseMetaData metadata = conn.getMetaData();
            List<String> names = new ArrayList<>();
            String[] types = {"TABLE"};
            ResultSet rs = metadata.getTables(null, null, "%", types);
            while(rs.next())
            {
                names.add(rs.getString(3)); // column 3 is table name
            }
            rs.close();
            return names;
        }
        catch(SQLException e)
        {
            controller.showDialog("Database Error", "Cannot get tables names", 0, 0);
            return null;
        }
    }

    public DatabaseUpdateView getDuv() {
        return duv;
    }

    public synchronized void shutdown()
    {
        try
        {
            DriverManager.getConnection(dbURL + ";shutdown=true");
            conn.close();
            //System.gc();
        }
        catch (SQLException sqlExcept) // TODO - obsluga
        {
            //sqlExcept.printStackTrace();
        }

    }
}
