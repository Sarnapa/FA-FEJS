package DatabaseService;

import java.sql.*;
import DataService.PlayerService;
import Layout.LeagueView;

import javax.activation.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

public class DatabaseConnection
{
    private static String dbURL = "jdbc:derby:./Database/DB;create=true;user=fafejs;password=fafejs";
    private Connection conn;
    private DatabaseUpdateContent duc;
    private DatabaseUpdateView duv;

    public synchronized void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // load the driver
            conn = DriverManager.getConnection(dbURL); // make Derby JDBC connection
            duc = new DatabaseUpdateContent(conn);
            duv = new DatabaseUpdateView(conn);
            System.out.println(conn);
        }
        catch (Exception except) // TODO - obsluga
        {
            except.printStackTrace();
        }
    }

    public synchronized void recreateConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(dbURL); // make Derby JDBC connection
            duc = new DatabaseUpdateContent(conn);
            duv = new DatabaseUpdateView(conn);
            System.out.println("Reconnect:" + conn);
        }

        catch(Exception e) // TODO - obsluga
        {
            e.printStackTrace();
        }

    }

    public synchronized boolean updatePlayer(PlayerService player)
    {
        try
        {
            int ID = player.getID();
            String firstName = player.getFirstName();
            String lastName = player.getLastName();
            Date birthdate;
            if(!(player.getDate() == null))
                birthdate =  new java.sql.Date(player.getDate().getTime());
            else
                birthdate = null;
            String league = newLeagueName(player.getLeagueName()).toUpperCase();
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
            return true;
        }
        catch (SQLIntegrityConstraintViolationException sqlE)
        {
            System.out.println(sqlE.getMessage());
            return true; // to avoid inserting bad data
        }
        catch (SQLException e)
        {
            //shutdown();
            while (e != null)
            {
                System.out.println("\n----- SQLException -----");
                System.out.println("  SQLState:   " + e.getSQLState());
                System.out.println("  Error Code: " + e.getErrorCode());
                System.out.println("  Message:    " + e.getMessage());
                e.printStackTrace(System.out);
                e = e.getNextException();
            }
            // for stack dumps, refer to derby.log or add
            //e.printStackTrace(System.out); above
            return false;
        }
    }

    private static String newLeagueName(String league) // String without ""
    {
        int first = league.indexOf('\"');
        if(first == -1)
            return league;
        int second = league.lastIndexOf('\"');
        StringBuffer sb = new StringBuffer(league.length() - 2);
        sb.append(league.substring(0, first)).append(league.substring(first + 1, second)).append(league.substring(second + 1));
        return sb.toString();
    }

    public synchronized void updateView(LeagueView view, String leagueName, String orderBy, boolean desc)
    {
        duv.updateView(view, leagueName, orderBy, desc);
    }

    public synchronized void shutdown()
    {
        try
        {
            DriverManager.getConnection(dbURL + ";shutdown=true");
            conn.close();
            //System.gc();
        }
        catch (SQLException sqlExcept)
        {
            //sqlExcept.printStackTrace();
        }

    }
}
