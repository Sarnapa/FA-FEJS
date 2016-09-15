package DatabaseService;

import java.sql.*;
import DataService.PlayerService;
import Layout.LeagueView;

public class DatabaseConnection
{
    private static String dbURL = "jdbc:derby:./Database/DB;create=true;user=fafejs;password=fafejs";
    private Connection conn;

    public synchronized void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // load the driver
            conn = DriverManager.getConnection(dbURL); // make Derby JDBC connection
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
            System.out.println("Reconnect:" + conn);
        }
        catch(Exception e)
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
            updatePlayersTable(ID, firstName, lastName, birthdate);
            updateLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
            return true;
        }
        catch (SQLException sqlExcept)
        {
            //shutdown();
            sqlExcept.printStackTrace();
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

    private void updatePlayersTable(int ID, String firstName, String lastName, Date birthdate) throws SQLException
    {
        System.out.println(ID + " " + firstName + " " + lastName);
        PreparedStatement pstmt = conn.prepareStatement("UPDATE APP.PLAYERS SET FIRST_NAME = ?, LAST_NAME = ?, BIRTHDATE = ? WHERE ID = ?");
        pstmt.setString(1,firstName);
        pstmt.setString(2,lastName);
        pstmt.setDate(3,birthdate);
        pstmt.setInt(4,ID);
        if(pstmt.executeUpdate() == 0)
        {
            pstmt = conn.prepareStatement("INSERT INTO APP.PLAYERS VALUES (?,?,?,?)");
            pstmt.setInt(1, ID);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setDate(4, birthdate);
            pstmt.execute();
        }
        pstmt.close();
    }

    private void updateLeagueTable(String league, int ID, String team, int apps, int firstSquad, int minutes, int goals, int yellowCards, int redCards) throws SQLException
    {
        String statement = "SELECT COUNT(*) AS cnt FROM TABLENAME WHERE ID = ?";
        statement = statement.replace("TABLENAME", "APP.\"" + league + "\"");
        //System.out.println(statement);
        PreparedStatement pstmt = conn.prepareStatement(statement);
        pstmt.setInt(1,ID);
        ResultSet result = pstmt.executeQuery();
        int resultCount = 0;
        while(result.next())
            resultCount = result.getInt("cnt");
        if(resultCount == 0)
        {
            insertPlayerToLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
        }
        else if(resultCount == 1)
        {
            if(isTheSameTeam(league, ID, team))
            {
                statement = "UPDATE TABLENAME SET APPS = ?, FIRST_SQUAD = ?, MINUTES = ?, GOALS = ?, YELLOW_CARDS = ?, RED_CARDS = ? WHERE ID = ?";
                statement = statement.replace("TABLENAME", "APP.\"" + league + "\"");
                pstmt = conn.prepareStatement(statement);
                pstmt.setInt(1, apps);
                pstmt.setInt(2, firstSquad);
                pstmt.setInt(3, minutes);
                pstmt.setInt(4, goals);
                pstmt.setInt(5, yellowCards);
                pstmt.setInt(6, redCards);
                pstmt.setInt(7, ID);
                pstmt.executeUpdate();
            }
            else
            {
                insertPlayerToLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
            }
        }
        else
        {
            statement = "UPDATE TABLENAME SET APPS = ?, FIRST_SQUAD = ?, MINUTES = ?, GOALS = ?, YELLOW_CARDS = ?, RED_CARDS = ? WHERE ID = ? AND TEAM = ?";
            statement = statement.replace("TABLENAME", "APP.\"" + league + "\"");
            pstmt = conn.prepareStatement(statement);
            pstmt.setInt(1, apps);
            pstmt.setInt(2, firstSquad);
            pstmt.setInt(3, minutes);
            pstmt.setInt(4, goals);
            pstmt.setInt(5, yellowCards);
            pstmt.setInt(6, redCards);
            pstmt.setInt(7, ID);
            pstmt.setString(8, team);
            pstmt.executeUpdate();
        }
        pstmt.close();
    }

    private boolean isTheSameTeam(String league, int ID, String team) throws SQLException
    {
        String statement = "SELECT TEAM AS cnt FROM TABLENAME WHERE ID = ?";
        statement = statement.replace("TABLENAME", "APP.\"" + league + "\"");
        PreparedStatement pstmt = conn.prepareStatement(statement);
        pstmt.setInt(1, ID);
        ResultSet result = pstmt.executeQuery();
        String resultTeam = "";
        while(result.next())
            resultTeam = result.getString(1);
        return resultTeam.equals(team);
    }

    private void insertPlayerToLeagueTable(String league, int ID, String team, int apps, int firstSquad, int minutes, int goals, int yellowCards, int redCards) throws SQLException
    {
        String statement = "INSERT INTO TABLENAME (ID, TEAM, APPS, FIRST_SQUAD, MINUTES, GOALS, YELLOW_CARDS, RED_CARDS) VALUES (?,?,?,?,?,?,?,?)";
        statement = statement.replace("TABLENAME", "APP.\"" + league + "\"");
        PreparedStatement pstmt = conn.prepareStatement(statement);
        pstmt.setInt(1, ID);
        pstmt.setString(2, team);
        pstmt.setInt(3, apps);
        pstmt.setInt(4, firstSquad);
        pstmt.setInt(5, minutes);
        pstmt.setInt(6, goals);
        pstmt.setInt(7, yellowCards);
        pstmt.setInt(8, redCards);
        pstmt.execute();
    }

    public void selectPlayers()
    {
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM APP.PLAYERS");
            ResultSetMetaData rsmd = results.getMetaData();//column names
            int numberCols = rsmd.getColumnCount();
            for (int i=0; i<numberCols; i++)
            {
                System.out.print(rsmd.getColumnLabel(i)+"\t\t");
            }

            System.out.println("\n-------------------------------------------------");

            while(results.next())
            {
                int id = results.getInt(1);
                String name = results.getString(2);
                String surname = results.getString(3);
                System.out.println(id + "\t\t" + name + "\t\t" + surname);
            }
            results.close();
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }

    public void updateView(LeagueView view, String leagueName, String orderBy, boolean desc){
        try
        {
            Statement stmt = conn.createStatement();
            String query;

            query = "SELECT * FROM APP.PLAYERS NATURAL JOIN APP.\"" + leagueName + "\"";
            if(!orderBy.equals("")) {
                query = query + " ORDER BY " + orderBy;
                if(desc)
                    query = query + " DESC";
            }
            ResultSet results = stmt.executeQuery(query);

            while(results.next())
            {
                String[] temp = {Integer.toString(results.getInt(1)), results.getString(2), results.getString(3), results.getString(4), results.getString(5),
                        Integer.toString(results.getInt(6)), Integer.toString(results.getInt(7)), Integer.toString(results.getInt(8)), Integer.toString(results.getInt(9)),
                        Integer.toString(results.getInt(10)), Integer.toString(results.getInt(11))};

                //view.addToPlayersList(results.getInt(1)+" "+results.getString(2)+" "+results.getInt(3)+" "+results.getInt(4)+" "+results.getInt(5)+" "+results.getInt(6)+" "+results.getInt(7)+" "+results.getInt(8));
                view.addToPlayersTable(temp);
            }
            results.close();
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
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
