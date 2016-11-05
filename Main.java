import DataService.PlayerService;
import Layout.*;
import DataService.LeaguesLinks;

public class Main {

    public static void main(String [] args)
    {
        System.out.println("elo");
        //LayoutInit layout = new LayoutInit();


        LeaguesLinks links = new LeaguesLinks();
        links.getLeaguesUrls();
        links.printYouthDivisionUrls();

        //PlayerService player = new PlayerService("Ekstraklasa", "ZAGŁĘBIE LUBIN", "https://www.laczynaspilka.pl/zawodnik/maciej-dabrowski,287974.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/michal-kucharczyk,93963.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/mihail-aleksandrov,1212442.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/bartosz-bereszynski,53781.html");
        //PlayerService player = new PlayerService("TRZECIA LIGA \"GRUPA II\"","GÓRNIK KONIN","https://www.laczynaspilka.pl/wizytowka/mateusz-augustyniak,226597.html");
        //PlayerService player = new PlayerService("TRZECIA LIGA \"GRUPA II\"","SOKÓŁ KLECZEW","https://www.laczynaspilka.pl/zawodnik/marcel-koziorowski,645587.html");;
        //PlayerService player = new PlayerService("IV LIGA GRUPA 1", "CZWARTA LIGA MAZOWIECKIE GRUPA I", "GKS POGOŃ GRODZISK MAZ.","https://www.laczynaspilka.pl/zawodnik/jan-balawejder,651784.html");;
        //PlayerService player = new PlayerService("", "", "", "https://www.laczynaspilka.pl/zawodnik/rafal-glod,956472.html");
        //player.getPlayerData();
        //player.printPlayerData();
    }
}
