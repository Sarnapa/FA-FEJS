import DataService.*;

public class Main {

    public static void main(String [] args)
    {
        LeagueService league = new LeagueService("https://www.laczynaspilka.pl/druzyny/ekstraklasa,1.html");
        league.getTeamUrls();
        league.writeTeamUrls();
    }
}