package DataService;

import DatabaseService.DatabaseConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class LeaguesLinks extends HtmlService
{
    private static final String url = "https://www.laczynaspilka.pl/";
    private LinkedList<String> leaguesUrls = new LinkedList<>(); // Ekstraklasa, 1 Liga, 2 Liga, 3 Liga, CLJ
    private HashMap<String, String> fourthDivision = new HashMap<String, String>(); // <table_name, url>

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
                }
            }
            get4LeagueUrls();
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

    private void get4LeagueUrls()
    {
        try
        {
            File file = new File("4liga.txt");
            Scanner fileReading = new Scanner(file);
            String tableName, url;
            while (fileReading.hasNextLine())
            {
                String line = fileReading.nextLine();
                int firstColonIndex = line.indexOf(':');
                tableName = line.substring(0, firstColonIndex);
                url = line.substring(firstColonIndex + 1, line.length());
                fourthDivision.put(tableName, url);
            }
        }
        catch (FileNotFoundException e) // TODO - obsluga
        {
            e.printStackTrace();
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
            startLeagueThread(url, null);
        Iterator<String> keySetIterator = fourthDivision.keySet().iterator();
        while(keySetIterator.hasNext())
        {
            String tableName = keySetIterator.next();
            String url = fourthDivision.get(tableName);
            startLeagueThread(url, tableName);
        }
    }

    private static void startLeagueThread(String url, String tableName)
    {
        Runnable league = new LeagueService(url, tableName);
        Thread leagueThread = new Thread(league);
        leagueThread.start();
    }

    public void printLeaguesUrls()
    {
        for(String url: leaguesUrls)
        {
            System.out.println(url);
        }
    }

    public void printFourthDivisionUrls()
    {
        Iterator<String> keySetIterator = fourthDivision.keySet().iterator();
        while(keySetIterator.hasNext())
        {
            String tableName = keySetIterator.next();
            String url = fourthDivision.get(tableName);
            System.out.println(tableName + ": " + url);
        }
    }

}
