package DatabaseService;

import DataService.PlayerService;
import Layout.LeagueView;
import java.sql.*;
import java.util.List;

/**
 * Main class for database service. Generally, methods are divided into two specific groups:
 * first associated with updating content of database, second containing select methods -
 * necessary for updating view.
 */

public class DatabaseConnection {
    private static final String dbURL = "jdbc:derby:./DB;create=true";
    private Connection conn;
    private DatabaseUpdateContent duc;
    private DatabaseUpdateView duv;

    /**
     * To create connection to database.
     * Reconnection method is intended for another attempts in downloading players task.
     */

    public synchronized boolean createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // load the driver
            conn = DriverManager.getConnection(dbURL); // make Derby JDBC connection
            duc = new DatabaseUpdateContent(conn);
            duv = new DatabaseUpdateView(conn);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public synchronized boolean recreateConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(dbURL); // make Derby JDBC connection
            duc = new DatabaseUpdateContent(conn);
            duv = new DatabaseUpdateView(conn);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    /**
     * For updating one specific column that is editing by user.
     */

    public synchronized boolean updatePlayersSpecificColumn(int id, String team, String leagueName, String columnName, Object newValue){
        try
        {
            String _columnName = columnName.replace(" ","_");
            duc.updatePlayersSpecificColumn(id, team, leagueName, _columnName, newValue);
        }
        catch(SQLException e)
        {
            return false;
        }
        return true;
    }

    /**
     * To delete player from database. User choose this player in 'Edit Mode'.
     */

    public synchronized  boolean deletePlayer(int ID, String team, String leagueName)
    {
        try
        {
            duc.deletePlayer(ID, team, leagueName);
        }
        catch(SQLException e)
        {
            return false;
        }
        return true;
    }

    /**
     *  Method for downloading players task. To update content of database.
     */

    public synchronized void updatePlayer(PlayerService player) throws SQLException
    {
        String firstName = player.getFirstName();
        String lastName = player.getLastName();
        int ID = player.getID();
        Date birthdate;
        if (!(player.getDate() == null))
            birthdate = new java.sql.Date(player.getDate().getTime());
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
        duc.updatePlayersTable(ID, firstName, lastName, birthdate);
        duc.updateLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
    }

    /**
     * Inserting player data that have been inserted by user in 'Insert Mode'.
     */

    public synchronized void insertPlayer(Player player) throws SQLException
    {
        String firstName = player.getFirstName();
        String lastName = player.getLastName();
        Player.PlayerRow row = player.getPlayerRows().get(0);
        int ID = player.getID();
        Date birthdate;
        if (!(player.getDate() == null))
            birthdate = player.getDate();
        else
            birthdate = null;
        String league = row.getLeagueName().toUpperCase();
        String team = row.getTeamName();
        int apps = row.getApps();
        int firstSquad = row.getFirstSquad();
        int minutes = row.getMinutes();
        int goals = row.getGoals();
        int yellowCards = row.getYellowCards();
        int redCards = row.getRedCards();
        if(!duc.existsInPlayers(ID))
        {
            duc.insertToPlayersTable(ID, firstName, lastName, birthdate);
        }
        duc.insertPlayerToLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
    }

    public synchronized boolean updateView(LeagueView view, String leagueName, String orderBy, boolean desc)
    {
        try
        {
            duv.updateView(view, leagueName, orderBy, desc);
        }
        catch(SQLException sqlException)
        {
            return false;
        }
        return true;
    }

    public List<String> getTablesNames() {
        try
        {
            return duc.getTableNames();
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    public DatabaseUpdateView getDuv() {
        return duv;
    }

    /**
     * To shutdown database. REMEMBER - DriverManager.getConnection throws exception
     * when successfully shutting down derby.
     */

    public synchronized boolean shutdown()
    {
        try
        {
            conn.close();
            DriverManager.getConnection(dbURL + ";shutdown=true");
            //System.gc();
        }
        catch (SQLNonTransientConnectionException sqlExcept1)
        {
            return true;
        }
        catch (SQLException sqlExcept2)
        {
            return false;
        }
        return false;
    }
}
