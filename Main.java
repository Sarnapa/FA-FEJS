import DataService.*;

public class Main {

    public static void main(String [] args)
    {
        LeaguesLinks links = new LeaguesLinks();
        links.getLeaguesUrls();
        //LeagueService league = new LeagueService("https://www.laczynaspilka.pl/druzyny/ekstraklasa,1.html");
        //league.getTeamsUrls();
        //league.writeTeamsUrls();
    }
}
