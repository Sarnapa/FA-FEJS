package DatabaseService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Structure containing data of player from all leagues where he plays.
 */

public class Player {
    public class PlayerRow {
        private String teamName, leagueName;
        private int apps, firstSquad, minutes, goals, yellowCards, redCards;

        /**
         * Class representing player stats data from one team
         */

        public PlayerRow(String teamName, int apps, int firstSquad, int minutes, int goals,
                         int yellowCards, int redCards, String leagueName)
        {
            this.teamName = teamName;
            this.apps = apps;
            this.firstSquad = firstSquad;
            this.minutes = minutes;
            this.goals = goals;
            this.yellowCards = yellowCards;
            this.redCards = redCards;
            this.leagueName = leagueName;
        }

        public String getTeamName() {
            return teamName;
        }

        public int getApps() {
            return apps;
        }

        public int getFirstSquad() {
            return firstSquad;
        }

        public int getMinutes() {
            return minutes;
        }

        public int getGoals() {
            return goals;
        }

        public int getYellowCards() {
            return yellowCards;
        }

        public int getRedCards() {
            return redCards;
        }

        public String getLeagueName() {
            return leagueName;
        }

        void printPlayerRow() {
            System.out.println(Thread.currentThread().getId() + " " + teamName + " " + leagueName + " " + apps + " " + firstSquad + " " + minutes + " " + goals + " " + yellowCards + " " + redCards);
        }
    }

    private int ID;
    private String firstName, lastName;
    private Date date;
    private List<PlayerRow> playerRows = new ArrayList<>();

    public Player(int ID, String firstName, String lastName, Date date) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public int getID() { return ID; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDate() {
        return date;
    }

    public List<PlayerRow> getPlayerRows() {
        return playerRows;
    }

    public void addPlayerRow(String teamName, int apps, int firstSquad, int minutes, int goals,
                             int yellowCards, int redCards, String leagueName) {
        PlayerRow row = new PlayerRow(teamName, apps, firstSquad, minutes, goals, yellowCards, redCards, leagueName);
        playerRows.add(row);
    }

    public void printPlayer() {
        for (PlayerRow row : playerRows)
            row.printPlayerRow();
    }
}
