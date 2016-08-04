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
    private HashMap<String, String> leaguesMap = new HashMap<String, String>();// <name, url>
    //private List<String> leaguesUrls = new LinkedList<>();
    //private List<String> leaguesNames = new ArrayList<>();

    public LeaguesLinks(DatabaseConnection db){ this.db = db;}
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
                        leaguesMap.put(leagueName, url);
                        //leaguesUrls.add(url);
                        //leaguesNames.add(leagueName);
                        break;
                    case "I Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesMap.put(leagueName, url);
                        //leaguesUrls.add(url);
                        //leaguesNames.add(leagueName);
                        break;
                    case "II Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesMap.put(leagueName, url);
                        //leaguesUrls.add(url);
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
            String leagueName = link.text().toUpperCase();
            if(!(leagueName.equals("Trzecia Liga") || leagueName.equals("Centralna Liga Juniorów \"Faza Finałowa\"")))
            {
                leaguesMap.put(leagueName, url + link.attr("href"));
                //leaguesUrls.add(url + link.attr("href"));
                //leaguesNames.add(leagueName);
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
        Iterator<String> keySetIterator = leaguesMap.keySet().iterator();
        while(keySetIterator.hasNext())
        {
            String name = keySetIterator.next();
            String url = leaguesMap.get(name);
            Runnable league = new LeagueService(name, url, db);
            Thread leagueThread = new Thread(league);
            leagueThread.start();
        }
    }

    public void printLeaguesNames()
    {
        Iterator<String> keySetIterator = leaguesMap.keySet().iterator();
        while(keySetIterator.hasNext())
        {
            System.out.println(keySetIterator.next());
        }
    }

    public void printLeaguesUrls()
    {
        Iterator<String> keySetIterator = leaguesMap.keySet().iterator();
        while(keySetIterator.hasNext())
        {
            String name = keySetIterator.next();
            String url = leaguesMap.get(name);
            System.out.println(url);
        }
    }
}
