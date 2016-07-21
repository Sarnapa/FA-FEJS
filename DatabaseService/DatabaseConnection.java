package DatabaseService;

import java.sql.*;


public class DatabaseConnection
{
    private static String dbURL = "jdbc:derby:./DatabaseService/DB;create=true;user=fafejs;password=fafejs";
    private static Connection conn = null;
    private static Statement stmt = null;

    public static synchronized void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection(dbURL);
            System.out.println(conn);
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }

    public static synchronized boolean insertPlayer(int id, String name, String surname, String dateOfBirth)
    {
        try
        {
            System.out.println(id + " " + name + " " + surname);
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO APP.PLAYERS VALUES (?,?,?)");
            pstmt.setInt(1,id);
            pstmt.setString(2,name);
            pstmt.setString(3,surname);
            pstmt.execute();
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
