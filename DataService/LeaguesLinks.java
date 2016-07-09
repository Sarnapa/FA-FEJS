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
    private List<String> leaguesUrls = new LinkedList<>();
    private List<String> leaguesNames = new LinkedList<>();

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
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
        getLeagues();
    }

    private void getSomeUrls(String url) throws IOException
    {
        Document doc = getHtmlSource(url);
        Element list = doc.getElementById("games");
        Elements links = list.getElementsByTag("a");
        for (Element link: links)
        {
            String leagueName = link.text();
            if(!(leagueName.equals("Trzecia liga") || leagueName.equals("Centralna Liga Juniorów \"Faza Finałowa\"")))
            {
                leaguesUrls.add(url + link.attr("href"));
                leaguesNames.add(leagueName);
            }
        }
    }

    /*private void getSomeUrlsForYouthDivisions() throws IOException
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
        for(String league: leaguesUrls)
        {
            System.out.println(league);
        }
    }

    public void printLeaguesNames()
    {
        for(String name: leaguesNames)
        {
            System.out.println(name);
        }
    }
}
