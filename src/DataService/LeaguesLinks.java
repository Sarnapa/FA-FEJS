package DataService;

import Layout.LayoutInit;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class LeaguesLinks implements Runnable {
    private static final String url = "https://www.laczynaspilka.pl/";
    private HashMap<String, String> leaguesMap = new HashMap<>(); // <table_name, url> - Ekstraklasa, 1 Liga, 2 Liga, 3 Liga, CLJ
    private HashMap<String, String> fourthDivision = new HashMap<>(); // <table_name, url>
    private HashMap<String, String> youthDivision = new HashMap<>(); // <table_name, url
    private static final int THREADS_NUMBER = 10;
    private List<LeagueService> threadsList = new ArrayList<>();
    private static final Object someObject = new Object();
    private List<String> selectedLeagues;
    private LayoutInit controller;
    private boolean isInterrupted;

    public LeaguesLinks(LayoutInit _controller) {
        controller = _controller;
    }

    public void setSelectedLeagues(List<String> list) {
        selectedLeagues = list;
    }

    public void clear() {
        leaguesMap.clear();
        fourthDivision.clear();
        youthDivision.clear();
        threadsList.clear();
    }

    public void run() {
        isInterrupted = false;
        Document doc = HtmlService.getHtmlSource(url, false, controller);
        if (doc != null) {
            Element menu = doc.getElementsByClass("main-category").first(); // one element
            Element leaguesMenu = menu.child(5); // 6 in menu (not 5 because we have one extra tags <li>)
            Elements leagueSpans = leaguesMenu.getElementsByTag("span");
            for (Element span : leagueSpans) {
                String leagueName = span.text(); // this leagueName is only valid on the main page ('I Liga and II liga' case)
                Element leagueUl = span.nextElementSibling();
                String url;
                switch (leagueName) {
                    case "Ekstraklasa":
                        url = leagueUl.child(2).child(0).attr("href");
                        if (selectedLeagues.contains("EKSTRAKLASA"))
                            leaguesMap.put("EKSTRAKLASA", url);
                        break;
                    case "I Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        if (selectedLeagues.contains("PIERWSZA LIGA"))
                            leaguesMap.put("PIERWSZA LIGA", url);
                        break;
                    case "II Liga":
                        url = leagueUl.child(2).child(0).attr("href");
                        if (selectedLeagues.contains("DRUGA LIGA"))
                            leaguesMap.put("DRUGA LIGA", url);
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

    private void getSomeUrls(String url) {
        Document doc = HtmlService.getHtmlSource(url, false, controller);
        if (doc != null) {
            Element list = doc.getElementById("games");
            Elements links = list.getElementsByTag("a");
            for (Element link : links) {
                String leagueName = link.text();
                if (!(leagueName.equals("Trzecia Liga") || leagueName.equals("Centralna Liga Juniorów \"Faza Finałowa\""))) {
                    String tableName = newLeagueName(leagueName).toUpperCase();
                    if (selectedLeagues.contains(tableName))
                        leaguesMap.put(tableName, url + link.attr("href"));
                }
            }
        }
    }

    private void get4LeagueUrls() {
        getDataFromFile("4liga.txt");
    }

    private void getYouthLeagueUrls() {
        getDataFromFile("ligi_mlodziezowe.txt");
    }

    private void getDataFromFile(String fileName) {
        try {
            File file = new File(fileName);
            Scanner fileReading = new Scanner(file);
            String tableName, url;
            while (fileReading.hasNextLine()) {
                String line = fileReading.nextLine();
                int firstColonIndex = line.indexOf(':');
                tableName = line.substring(0, firstColonIndex).toUpperCase();
                url = line.substring(firstColonIndex + 1, line.length());
                if (selectedLeagues.contains(tableName)) {
                    if (fileName.equals("4liga.txt"))
                        fourthDivision.put(tableName, url);
                    else
                        youthDivision.put(tableName, url);
                }
            }
        } catch (FileNotFoundException e) {
            controller.log("File " + fileName + " not found.", 1);
        }
    }

    private void getLeagues() {
        try {
            int currentThreadsNumber = 0;
            synchFunction(leaguesMap, currentThreadsNumber, true, false);
            synchFunction(fourthDivision, currentThreadsNumber, false, false);
            synchFunction(youthDivision, currentThreadsNumber, false, true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            controller.showDialog("Error", "The application will be closed", 0, 2);
            System.exit(1);
        }
    }

    private void startLeagueThread(String url, String tableName, boolean isNormalLeague, boolean isJSON) {
        LeagueService leagueThread = new LeagueService(url, tableName, someObject, isNormalLeague, isJSON, controller);
        threadsList.add(leagueThread);
        leagueThread.start();
    }

    private void synchFunction(HashMap<String, String> map, int currentThreadsNumber, boolean isNormalLeague, boolean isJSON) throws InterruptedException {
        String tableName, url;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (isInterrupted)
                break;
            tableName = entry.getKey();
            url = entry.getValue();
            startLeagueThread(url, tableName, isNormalLeague, isJSON);
            ++currentThreadsNumber;
            if (currentThreadsNumber == THREADS_NUMBER) {
                synchronized (someObject) {
                    someObject.wait();
                }
                --currentThreadsNumber;
            }
        }
    }

    public void killLeagueThreads() {
        isInterrupted = true;
        for (LeagueService t : threadsList) {
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                controller.showDialog("Error", "The application will be closed", 0, 2);
                System.exit(1);
            }
        }
    }

    public boolean checkHostConnection() {
        try {
            //make a URL to a known source
            URL address = new URL(url);

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) address.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (IOException e) {
            controller.showDialog("No internet connection", "Check your internet connection.", 0, 0);
            return false;
        }
        return true;
    }

    private static String newLeagueName(String league) // String without ""
    {
        int first = league.indexOf('\"');
        if (first == -1)
            return league;
        int second = league.lastIndexOf('\"');
        StringBuffer sb = new StringBuffer(league.length() - 2);
        sb.append(league.substring(0, first)).append(league.substring(first + 1, second)).append(league.substring(second + 1));
        return sb.toString();
    }

    private void printLeaguesMap() {
        String tableName, url;
        for (Map.Entry<String, String> entry : leaguesMap.entrySet()) {
            tableName = entry.getKey();
            url = entry.getValue();
            System.out.println(tableName + ": " + url);
        }
        System.out.println("Normal Division Number: " + leaguesMap.size());
    }

    private void printFourthDivisionUrls() {
        String tableName, url;
        for (Map.Entry<String, String> entry : leaguesMap.entrySet()) {
            tableName = entry.getKey();
            url = entry.getValue();
            System.out.println(tableName + ": " + url);
        }
        System.out.println("Fourth Division Number: " + leaguesMap.size());
    }

    private void printYouthDivisionUrls() {
        String tableName, url;
        for (Map.Entry<String, String> entry : leaguesMap.entrySet()) {
            tableName = entry.getKey();
            url = entry.getValue();
            System.out.println(tableName + ": " + url);
        }
        System.out.println("Youth Division Number: " + youthDivision);
    }

    public void printAllLeagues() {
        System.out.println("Upper Division and CLJ");
        printLeaguesMap();
        System.out.println("4 Divisions");
        printFourthDivisionUrls();
        System.out.println("Another Youth Divisions");
        printYouthDivisionUrls();
    }

}
