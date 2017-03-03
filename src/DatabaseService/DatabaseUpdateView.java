package DatabaseService;

import DataService.PlayerService;
import Layout.LeagueView;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatabaseUpdateView
{
    private Connection conn;

    public DatabaseUpdateView(Connection conn)
    {
        this.conn = conn;
    }

    /*public void selectPlayers()
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
    }*/

    public Player getPlayerRows(int ID, List<String> leaguesNames)
    {
        Player player = null;
        for(String leagueName: leaguesNames)
        {
            if(!leagueName.equals("PLAYERS"))
            {
                String query = "SELECT * FROM APP.PLAYERS NATURAL JOIN APP.\"" + leagueName + "\" WHERE ID = ?";
                try
                {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, ID);
                    ResultSet results = pstmt.executeQuery();
                    while (results.next())
                    {
                        if(player == null)
                            player = new Player(results.getInt(1), results.getString(2), results.getString(3), results.getDate(4));
                        player.addPlayerRow(results.getString(5), results.getInt(6), results.getInt(7), results.getInt(8),
                                results.getInt(9), results.getInt(10), results.getInt(11), leagueName);
                    }
                }
                catch (SQLException e) // TODO - obsluga
                {
                    e.printStackTrace();
                }
            }
        }
        player.printPlayer();
        return player;
    }

    public void updateView(LeagueView view, String leagueName, String orderBy, boolean desc)
    {
        try
        {
            Statement stmt = conn.createStatement();
            String query = prepareStatement(leagueName, orderBy, desc);

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

    private static String prepareStatement(String leagueName, String orderBy, boolean desc)
    {
        String query = "SELECT * FROM APP.PLAYERS NATURAL JOIN APP.\"" + leagueName + "\"";
        if(!orderBy.equals("")) {
            query = query + " ORDER BY " + orderBy;
            if(desc)
                query = query + " DESC";
        }
        return query;
    }
}
