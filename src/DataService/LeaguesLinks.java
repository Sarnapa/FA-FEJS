package DataService;

import DatabaseService.DatabaseConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class LeaguesLinks extends HtmlService
{
    private static final String url = "https://www.laczynaspilka.pl/";
    private HashMap<String, String> leaguesMap = new HashMap<String, String>(); // <table_name, url> - Ekstraklasa, 1 Liga, 2 Liga, 3 Liga, CLJ
    private HashMap<String, String> fourthDivision = new HashMap<String, String>(); // <table_name, url>
    private HashMap<String, String> youthDivision = new HashMap<String, String>(); // <table_name, url>
    private static final int THREADS_NUMBER = 5;
    //private static final Semaphore mutex = new Semaphore(0);
    private static final Object someObject = new Object();

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
            //for(int i = 0; i < leagueSpans.size(); ++i)
            for (Element span : leagueSpans)
            {
                //Element span = leagueSpans.get(i);
                String leagueName = span.text(); // this leagueName is only valid on the main page ('I Liga and II liga' case)
                Element leagueUl = span.nextElementSibling();
                String url;
                switch (leagueName)
                {
                    case "Ekstraklasa":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesMap.put("Ekstraklasa", url);
                        //leaguesUrls.add(url);
                        //leaguesNames.add(leagueName);
                        break;
                    case "I Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesMap.put("Pierwsza Liga", url);
                        //leaguesUrls.add(url);
                        //leaguesNames.add(leagueName);
                        break;
                    case "II Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        leaguesMap.put("Druga Liga", url);
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
                }
            }
            get4LeagueUrls();
            getYouthLeagueUrls();
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
            //for(int i = 0; i < links.size(); ++i)
            for (Element link : links)
            {
                //Element link = links.get(i);
                String leagueName = link.text();
                if (!(leagueName.equals("Trzecia Liga") || leagueName.equals("Centralna Liga Juniorów \"Faza Finałowa\"")))
                {
                    leaguesMap.put(leagueName, url + link.attr("href"));
                    //leaguesUrls.add(url + link.attr("href"));
                    //leaguesNames.add(leagueName);
                }
            }
        }
    }

    private void get4LeagueUrls()
    {
        getDataFromFile("4liga.txt");
    }

    private void getYouthLeagueUrls()
    {
        getDataFromFile("ligi_mlodziezowe.txt");
    }

    private void getDataFromFile(String fileName)
    {
        try
        {
            File file = new File(fileName);
            Scanner fileReading = new Scanner(file);
            String tableName, url;
            while (fileReading.hasNextLine())
            {
                String line = fileReading.nextLine();
                int firstColonIndex = line.indexOf(':');
                tableName = line.substring(0, firstColonIndex);
                url = line.substring(firstColonIndex + 1, line.length());
                //StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                //System.out.println(stackTraceElements[2].getMethodName());
                //if(stackTraceElements[1].getMethodName().equals("get4LeagueUrls"))
                if(fileName.equals("4liga.txt"))
                    fourthDivision.put(tableName, url);
                else
                    youthDivision.put(tableName, url);
            }
        }
        catch (FileNotFoundException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
    }

    private void getLeagues()
    {
        try
        {
            System.out.println(leaguesMap.size());
            int currentThreadsNumber = 0;
            String tableName, url;
            Iterator<String> keySetIterator = leaguesMap.keySet().iterator();
            while (keySetIterator.hasNext())
            {
                tableName = keySetIterator.next();
                url = leaguesMap.get(tableName);
                startLeagueThread(url, tableName, true);
                ++currentThreadsNumber;
                if (currentThreadsNumber == THREADS_NUMBER)
                {
                    synchronized(someObject)
                    {
                        System.out.println("Blokada");
                        someObject.wait();
                        System.out.println("Po blokadzie");
                    }
                    --currentThreadsNumber;
                }
            }
            keySetIterator = fourthDivision.keySet().iterator();
            while (keySetIterator.hasNext())
            {
                tableName = keySetIterator.next();
                url = fourthDivision.get(tableName);
                startLeagueThread(url, tableName, false);
                ++currentThreadsNumber;
                if (currentThreadsNumber == THREADS_NUMBER)
                {
                    synchronized(someObject)
                    {
                        System.out.println("Blokada");
                        someObject.wait();
                        System.out.println("Po blokadzie");
                    }
                    --currentThreadsNumber;
                }
            }
            /*keySetIterator = youthDivision.keySet().iterator();
            while (keySetIterator.hasNext())
            {
                tableName = keySetIterator.next();
                url = youthDivision.get(tableName);
                startLeagueThread(url, tableName, false);
                ++currentThreadsNumber;
                if (currentThreadsNumber == THREADS_NUMBER)
                {
                    synchronized(someObject)
                    {
                        System.out.println("Blokada");
                        someObject.wait();
                        System.out.println("Po blokadzie");
                    }
                    --currentThreadsNumber;
                }
            }*/
            //synchFunction(leaguesMap, currentThreadsNumber, true);
            //synchFunction(fourthDivision, currentThreadsNumber, false);
            //synchFunction(youthDivision, currentThreadsNumber, false);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void startLeagueThread(String url, String tableName, boolean isNormalLeague)
    {
        Runnable league = new LeagueService(url, tableName, someObject, isNormalLeague);
        Thread leagueThread = new Thread(league);
        leagueThread.start();
    }

    /**private void synchFunction(HashMap<String, String> map, int currentThreadsNumber, boolean isNormalLeague) throws InterruptedException
    {
        String tableName, url;
        Iterator<String> keySetIterator = map.keySet().iterator();
        while (keySetIterator.hasNext())
        {
            tableName = keySetIterator.next();
            url = map.get(tableName);
            startLeagueThread(url, tableName, isNormalLeague);
            ++currentThreadsNumber;
            if (currentThreadsNumber == THREADS_NUMBER)
            {
                synchronized(someObject)
                {
                    System.out.println("Blokada");
                    someObject.wait();
                    System.out.println("Po blokadzie");
                }
                --currentThreadsNumber;
            }
        }
    }**/

    public void printLeaguesMap()
    {
        Iterator<String> keySetIterator = leaguesMap.keySet().iterator();
        int i = 0;
        while(keySetIterator.hasNext())
        {
            String tableName = keySetIterator.next();
            String url = leaguesMap.get(tableName);
            System.out.println(tableName + ": " + url);
            ++i;
        }
        System.out.println("Normal Division Number: " + i);
    }

    public void printFourthDivisionUrls()
    {
        Iterator<String> keySetIterator = fourthDivision.keySet().iterator();
        int i = 0;
        while(keySetIterator.hasNext())
        {
            String tableName = keySetIterator.next();
            String url = fourthDivision.get(tableName);
            System.out.println(tableName + ": " + url);
            ++i;
        }
        System.out.println("Fourth Division Number: " + i);
    }

    public void printYouthDivisionUrls()
    {
        Iterator<String> keySetIterator = youthDivision.keySet().iterator();
        int i = 0;
        while(keySetIterator.hasNext())
        {
            String tableName = keySetIterator.next();
            String url = youthDivision.get(tableName);
            System.out.println(tableName + ": " + url);
            //System.out.println(i + " " + tableName + ": " + url);
            ++i;
        }
        System.out.println("Youth Division Number: " + i);
    }

    public void printAllLeagues()
    {
        System.out.println("Upper Division and CLJ");
        printLeaguesMap();
        System.out.println("4 Divisions");
        printFourthDivisionUrls();
        System.out.println("Another Youth Divisions");
        printYouthDivisionUrls();
    }

}
