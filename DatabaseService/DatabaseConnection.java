package DatabaseService;

import java.sql.*;
import DataService.PlayerService;


public class DatabaseConnection
{
    private static String dbURL = "jdbc:derby:./Database/DB;create=true;user=fafejs;password=fafejs";
    private static Connection conn = null;
    private static Statement stmt = null;

    public static synchronized void createConnection()
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

    public static synchronized boolean updatePlayer(PlayerService player)
    {
        try
        {
            int ID = player.getID();
            String firstName = player.getFirstName();
            String lastName = player.getLastName();
            Date birthdate =  new java.sql.Date(player.getDate().getTime());
            System.out.println(ID + " " + firstName + " " + lastName);
            PreparedStatement pstmt = conn.prepareStatement("UPDATE APP.PLAYERS SET FIRST_NAME = ?, LAST_NAME = ?, BIRTHDATE = ? WHERE ID = ?");
            pstmt.setString(1,firstName);
            pstmt.setString(2,lastName);
            pstmt.setDate(3,birthdate);
            pstmt.setInt(4,ID);
            if(pstmt.executeUpdate() < 1)
            {
                pstmt = conn.prepareStatement("INSERT INTO APP.PLAYERS VALUES (?,?,?,?)");
                pstmt.setInt(1, ID);
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setDate(4, birthdate);
                pstmt.execute();
            }
            pstmt.close();
            return true;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
            return false;
        }
    }

    public static void selectPlayers()
    {
        try
        {
            stmt = conn.createStatement();
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
    public static synchronized void shutdown()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                //System.gc();
                conn.close();
                conn = null;
            }
        }
        catch (SQLException sqlExcept)
        {
            //sqlExcept.printStackTrace();
        }

    }
}
