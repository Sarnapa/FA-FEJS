import DataService.*;
import DatabaseService.DatabaseConnection;

public class Main {
    public static void main(String [] args)
    {
        System.out.println("elo");
        LeaguesLinks links = new LeaguesLinks();
        links.getLeaguesUrls();
        //PlayerService player = new PlayerService("Ekstraklasa", "ZAGŁĘBIE LUBIN", "https://www.laczynaspilka.pl/zawodnik/maciej-dabrowski,287974.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/michal-kucharczyk,93963.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/mihail-aleksandrov,1212442.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/bartosz-bereszynski,53781.html");
        //PlayerService player = new PlayerService("TRZECIA LIGA \"GRUPA II\" ","GÓRNIK KONIN","https://www.laczynaspilka.pl/wizytowka/mateusz-augustyniak,226597.html");
        //player.getPlayerData();
        //player.printPlayerData();
    }
}
