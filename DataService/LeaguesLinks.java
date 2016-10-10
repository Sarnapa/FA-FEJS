package DataService;

import DatabaseService.DatabaseConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

public class LeaguesLinks extends HtmlService
{
    private static final String url = "https://www.laczynaspilka.pl/";
    //private HashMap<String, String> leaguesMap = new HashMap<String, String>();// <name, url>
    private LinkedList<String> leaguesUrls = new LinkedList<>();

    public LeaguesLinks()
    {
    }

    public void getLeaguesUrls()
    {
        Document doc = getHtmlSource(url);
        if(doc != null)
        {
            Element menu = doc.getElementsByClass("main-category").first(); // one element
            Element leaguesMenu = menu.child(5); // 5 in menu (not 4 because we have one extra tags <li>)
            Elements leagueSpans = leaguesMenu.getElementsByTag("span");
            for (Element span : leagueSpans)
            {
                String leagueName = span.text(); // this leagueName is only valid on the main page ('I Liga and II liga' case)
                Element leagueUl = span.nextElementSibling();
                String url;
                switch (leagueName)
                {
                    case "Ekstraklasa":
                        url = leagueUl.child(2).child(0).attr("href");
                        //leaguesMap.put(leagueName, url);
                        leaguesUrls.add(url);
                        //leaguesNames.add(leagueName);
                        break;
                    case "I Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        //leaguesMap.put(leagueName, url);
                        leaguesUrls.add(url);
                        //leaguesNames.add(leagueName);
                        break;
                    case "II Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        //leaguesMap.put(leagueName, url);
                        leaguesUrls.add(url);
                        //leaguesNames.add(leagueName);
                        break;
                    case "III Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        getSomeUrls(url);
                        break;
                    case "CLJ":
                        url = leagueUl.child(2).child(0).attr("href");
                        getSomeUrls(url);
                        break;
                    case "Dzieci i Młodzież": // problem :(
                        //getSomeUrlsForYouthDivisions();
                        break;
                }
            }
            getLeagues();
        }
    }

    private void getSomeUrls(String url)
    {
        Document doc = getHtmlSource(url);
        if(doc != null)
        {
            Element list = doc.getElementById("games");
            Elements links = list.getElementsByTag("a");
            for (Element link : links) {
                String leagueName = link.text();
                if (!(leagueName.equals("Trzecia Liga") || leagueName.equals("Centralna Liga Juniorów \"Faza Finałowa\""))) {
                    //leaguesMap.put(leagueName, url + link.attr("href"));
                    leaguesUrls.add(url + link.attr("href"));
                    //leaguesNames.add(leagueName);
                }
            }
        }
    }

    /*private void getSomeUrlsForYouthDivisions()
    {
        for(int i = 1; i <= 16; ++i)
        {
            Document doc = getHtmlSource(url + "#" + i);
        }
    }*/

    private void getLeagues()
    {
        for(String url: leaguesUrls)
        {
            Runnable league = new LeagueService(url);
            Thread leagueThread = new Thread(league);
            leagueThread.start();
        }
    }

    public void printLeaguesUrls()
    {
        for(String url: leaguesUrls)
        {
            System.out.println(url);
        }
    }
}
