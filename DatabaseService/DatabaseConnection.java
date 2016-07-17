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
            stmt.execute("insert into PLAYERS values (" +
                    id + ",'" + name + "','" + surname + "')");
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
                conn.close();
            }
        }
        catch (SQLException sqlExcept)
        {
            //sqlExcept.printStackTrace();
        }

    }
}
