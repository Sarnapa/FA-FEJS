package DatabaseService;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class DatabaseUpdateContent {
    private final Connection conn;

    DatabaseUpdateContent(Connection conn) {
        this.conn = conn;
    }

    void updatePlayersTable(int ID, String firstName, String lastName, Date birthdate) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("UPDATE APP.PLAYERS SET FIRST_NAME = ?, LAST_NAME = ?, BIRTHDATE = ? WHERE ID = ?");
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.setDate(3, birthdate);
        pstmt.setInt(4, ID);
        if (pstmt.executeUpdate() == 0) {
            pstmt = conn.prepareStatement("INSERT INTO APP.PLAYERS VALUES (?,?,?,?)");
            pstmt.setInt(1, ID);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setDate(4, birthdate);
            pstmt.execute();
        }
        pstmt.close();
    }

    void updateLeagueTable(String league, int ID, String team, int apps, int firstSquad, int minutes, int goals, int yellowCards, int redCards) throws SQLException {
        String statement = "SELECT COUNT(*) AS cnt FROM TABLENAME WHERE ID = ?";
        statement = statement.replace("TABLENAME", "APP.\"" + league + "\"");
        PreparedStatement pstmt = conn.prepareStatement(statement);
        pstmt.setInt(1, ID);
        ResultSet result = pstmt.executeQuery();
        int resultCount = 0;
        while (result.next())
            resultCount = result.getInt("cnt");
        if (resultCount == 0) {
            insertPlayerToLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
        } else if (resultCount == 1) {
            if (isTheSameTeam(league, ID, team)) {
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
            } else {
                insertPlayerToLeagueTable(league, ID, team, apps, firstSquad, minutes, goals, yellowCards, redCards);
            }
        } else {
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

    private boolean isTheSameTeam(String league, int ID, String team) throws SQLException {
        String statement = "SELECT TEAM AS cnt FROM TABLENAME WHERE ID = ?";
        statement = statement.replace("TABLENAME", "APP.\"" + league + "\"");
        PreparedStatement pstmt = conn.prepareStatement(statement);
        pstmt.setInt(1, ID);
        ResultSet result = pstmt.executeQuery();
        String resultTeam = "";
        while (result.next())
            resultTeam = result.getString(1);
        return resultTeam.equals(team);
    }

    private void insertPlayerToLeagueTable(String league, int ID, String team, int apps, int firstSquad, int minutes, int goals, int yellowCards, int redCards) throws SQLException {
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

    public void updatePlayersSpecificColumn(int id, String team, String leagueName, String columnName, Object newValue) throws SQLException {
        String statement = "UPDATE TABLENAME SET " + columnName + " = ? WHERE ID = ?";
        if (leagueName.equals("")){   //change data in PLAYERS
            statement = statement.replace("TABLENAME", "APP.PLAYERS");
        } else {                        //change data in league table
            statement = statement.replace("TABLENAME", "APP.\"" + leagueName + "\"");
            statement = statement + "AND TEAM = ?";
        }
        PreparedStatement pstmt = conn.prepareStatement(statement);
        if(newValue.getClass() == Integer.class) {
            pstmt.setInt(1, Integer.parseInt(newValue.toString()));
        }
        else if(newValue.getClass() == String.class) {
            pstmt.setString(1, newValue.toString());
        }
        else if(newValue.getClass() == java.sql.Date.class){
            pstmt.setDate(1, (java.sql.Date)newValue);
        }
        pstmt.setInt(2, id);
        if(!team.equals("")){
            pstmt.setString(3, team);
        }
        pstmt.execute();
    }
}
