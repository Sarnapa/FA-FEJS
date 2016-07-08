import DataService.*;

public class Main {

    public static void main(String [] args)
    {
        LeaguesLinks links = new LeaguesLinks();
        links.getLeaguesUrls();
        //TeamService team = new TeamService("https://www.laczynaspilka.pl/druzyna/gks-piast-s-a-gliwice,112982.html");
        //team.getPlayersUrls();
        //team.printPlayersUrls();
        //LeagueService league = new LeagueService("https://www.laczynaspilka.pl/druzyny/ekstraklasa,1.html");
        //league.getTeamsUrls();
        //league.printTeamsUrls();
    }
}
