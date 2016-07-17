import DataService.*;
import DatabaseService.DatabaseConnection;

import java.io.PrintStream;

public class Main {
    static DatabaseConnection database = new DatabaseConnection();
    public static void main(String [] args)
    {
        //PrintStream printStream = new PrintStream(new CustomOutputStream());
        //System.setOut(printStream);
        //System.setErr(printStream);

        database.createConnection();
        System.out.println("elo");
        LeaguesLinks links = new LeaguesLinks(database);
        links.getLeaguesUrls();
        database.shutdown();
        //TeamService team = new TeamService("https://www.laczynaspilka.pl/druzyna/gks-piast-s-a-gliwice,112982.html");
        //team.getPlayersUrls();
        //team.printPlayersUrls();
        //LeagueService league = new LeagueService("https://www.laczynaspilka.pl/druzyny/ekstraklasa,1.html");
        //league.getTeamsUrls();
        //league.printTeamsUrls();
        //PlayerService player = new PlayerService("https://www.laczynaspilka.pl/zawodnik/lukasz-adamczak,788487.html");
        //player.getPlayerData();
        //player.printPlayerData();
    }
}
