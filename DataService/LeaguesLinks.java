package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LeaguesLinks extends HtmlService
{
    private static final String url = "https://www.laczynaspilka.pl/";
    private List<String> leaguesUrls = new LinkedList<String>();
    private List<String> leaguesNames = new LinkedList<String>();

    public void getLeaguesUrls()
    {
        try
        {
            Document doc = getHtmlSource(url);
            Element menu = doc.getElementsByClass("main-category").first(); // one element
            Element leaguesMenu = menu.child(5); // 5 in menu (not 4 because we have one extra tags <li>)
            Elements leagueSpans = leaguesMenu.getElementsByTag("span");
            for (Element span : leagueSpans)
            {
                String leagueName = span.text();
                Element leagueUl = span.nextElementSibling();
                String url;
                switch (leagueName)
                {
                    case "Ekstraklasa":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesUrls.add(url);
                        leaguesNames.add(leagueName);
                        break;
                    case "I Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesUrls.add(url);
                        leaguesNames.add(leagueName);
                        break;
                    case "II Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesUrls.add(url);
                        leaguesNames.add(leagueName);
                        break;
                    case "III Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        break;
                    case "CLJ":
                        url = leagueUl.child(2).child(0).attr("href");
                        break;
                    case "Dzieci i Młodzież":
                        url = leagueUl.child(0).child(0).attr("href");
                        break;
                }
            }
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
        getLeagues();
    }

    private static void setBasicLeague()
    {

    }

    private void getLeagues()
    {
        for(String url: leaguesUrls)
        {
            LeagueService league = new LeagueService(url);
            league.getTeamsUrls();
            league.writeTeamUrls();
        }
    }

    public void writeLeaguesUrls()
    {
        for(String league: leaguesUrls)
        {
            System.out.println(league);
        }
    }

    public void writeLeaguesNames()
    {
        for(String name: leaguesNames)
        {
            System.out.println(name);
        }
    }
}
