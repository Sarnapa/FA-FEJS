import DataService.*;
import DatabaseService.DatabaseConnection;

import java.io.PrintStream;

public class Main {
    static DatabaseConnection database = new DatabaseConnection();
    public static void main(String [] args)
    {
        System.out.println("elo");
        LeaguesLinks links = new LeaguesLinks(database);
        links.getLeaguesUrls();
        //PlayerService player = new PlayerService("Ekstraklasa", "ZAGŁĘBIE LUBIN", "https://www.laczynaspilka.pl/zawodnik/maciej-dabrowski,287974.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/michal-kucharczyk,93963.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/mihail-aleksandrov,1212442.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/bartosz-bereszynski,53781.html");
        //player.getPlayerData();
        //player.printPlayerData();
    }
}
