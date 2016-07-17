package DatabaseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;


public class DatabaseConnection
{
    private static String dbURL = "jdbc:derby:./DatabaseService/DB;create=true;user=fafejs;password=fafejs";
    private static Connection conn = null;
    private static Statement stmt = null;

    public static void createConnection()
    {
        try
        {
            //System.gc();
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection(dbURL);
            System.out.println(conn);
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }

    public static synchronized void insertPlayer(int id, String name, String surname, String dateOfBirth)
    {
        try
        {
            stmt = conn.createStatement();
            System.out.println("elo2");
            stmt.execute("INSERT INTO APP.PLAYERS VALUES (" +
                    id + ",'" + name + "','" + surname + "')");
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
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
    public static void shutdown()
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
                conn = null;
                conn.close();
            }
        }
        catch (SQLException sqlExcept)
        {
            //sqlExcept.printStackTrace();
        }

    }
}
