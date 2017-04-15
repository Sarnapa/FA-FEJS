package DatabaseService;

import Layout.LeagueView;
import java.sql.*;
import java.util.List;

/**
 * Some methods that provide data from database to view.
 */

public class DatabaseUpdateView
{
    private final Connection conn;

    DatabaseUpdateView(Connection conn)
    {
        this.conn = conn;
    }

    public Player getPlayerRows(int ID, List<String> leaguesNames) throws SQLException
    {
        Player player = null;
        for (String leagueName : leaguesNames) {
            if (!leagueName.equals("PLAYERS"))
            {
                String query = "SELECT * FROM APP.PLAYERS NATURAL JOIN APP.\"" + leagueName + "\" WHERE ID = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, ID);
                ResultSet results = pstmt.executeQuery();
                while (results.next()) {
                    if (player == null)
                        player = new Player(results.getInt(1), results.getString(2), results.getString(3), results.getDate(4));
                    player.addPlayerRow(results.getString(5), results.getInt(6), results.getInt(7), results.getInt(8),
                            results.getInt(9), results.getInt(10), results.getInt(11), leagueName);
                }
            }
        }
        return player;
    }

    void updateView(LeagueView view, String leagueName, String orderBy, boolean desc) throws SQLException
    {
        Statement stmt = conn.createStatement();
        String query = prepareStatement(leagueName, orderBy, desc);

        ResultSet results = stmt.executeQuery(query);
        while (results.next()) {
            Object[] temp = {results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5),
                    results.getInt(6), results.getInt(7), results.getInt(8), results.getInt(9),
                    results.getInt(10), results.getInt(11)};

            view.addToPlayersTable(temp);
        }
        results.close();
        stmt.close();
    }

    private static String prepareStatement(String leagueName, String orderBy, boolean desc)
    {
        String query = "SELECT * FROM APP.PLAYERS NATURAL JOIN APP.\"" + leagueName + "\"";
        if (!orderBy.equals("")) {
            orderBy = orderBy.replace(" ", "_");
            query = query + " ORDER BY " + "\"" + orderBy + "\"";
            if (desc)
                query = query + " DESC";
        }
        return query;
    }
}
