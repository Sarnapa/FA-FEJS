package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class LeagueService extends HtmlService implements Runnable
{
    private String name; // League's name
    private String tableName; // sometimes League's name from website differs from table's name
    private String url; // League's URL
    //private static Semaphore mutex;
    private static Object someObject;
    private boolean isNormalLeague; // Normal league = Ekstraklasa, I liga, II liga, III Liga, CLJ
    private List<String> teamsUrls = new LinkedList<>(); // List of teams' Urls

    public LeagueService(String url, String tableName, Object someObject, boolean isNormalLeague)
    {
        this.url = url;
        this.tableName = tableName;
        this.someObject = someObject;
        this.isNormalLeague = isNormalLeague;
    }

    public void run()
    {
        //long startTime = System.currentTimeMillis();
        try
        {
            Document doc = getHtmlSource(url);
            if(doc != null)
            {
                if(isNormalLeague)
                {
                    name = doc.getElementsByClass("show-drop").first().text();
                    getUrls(doc);
                }
                else
                {
                    String firstWord = tableName.substring(0, tableName.indexOf(' '));
                    if(firstWord.equals("CZWARTA"))
                    {
                        String nameText = doc.getElementsByClass("header-menu").first().text();
                        name = nameText.substring(9, nameText.indexOf('(') - 1);
                        getUrls(doc);
                    }
                    else
                    {
                        String nameText = doc.getElementsByClass("name-year").first().text();
                        name = nameText.substring(0, nameText.lastIndexOf(':'));
                        getYouthTeamsUrls(doc);
                    }
                }
                getTeams();
                //System.out.println(System.currentTimeMillis() - startTime);
            }
        }
        catch (InterruptedException e)  // TODO - obsluga
        {
            //Thread.currentThread().interrupt();
        }
        finally
        {
            synchronized(someObject)
            {
                someObject.notify();
            }
        }
    }

    private void getUrls(Document doc)
    {
        Element teamsContainer = doc.getElementsByClass("league-teams-list").first(); // one element - cannot use getElementById. Not complete class name but it works
        Elements rows = teamsContainer.getElementsByClass("row");
        //for(int i = 0; i < rows.size(); ++i)
        for (Element row : rows)
        {
            //Element row = rows.get(i);
            Elements links = row.getElementsByTag("a");
            //for(int j = 0; j < links.size(); ++j)
            for (Element link : links)
            {
                //Element link = links.get(j);
                teamsUrls.add(link.attr("href"));
            }
        }
    }

    private void getYouthTeamsUrls(Document doc)
    {
        Element teamsTable = doc.getElementsByClass("table-template").first();
        Elements rows = teamsTable.getElementsByClass("row-link");
        //for(int i = 0; i < rows.size(); ++i)
        for (Element row : rows)
        {
            //Element row = rows.get(i);
            teamsUrls.add(row.attr("data-url"));
        }
    }

    private void getTeams() throws InterruptedException
    {
        for(String url: teamsUrls)
        {
            TeamService team = new TeamService(name, tableName, url);
            team.getPlayersUrls();
        }
        //printTeamUrls();
    }

    public void printTeamUrls()
    {
        for(String team: teamsUrls)
        {
            System.out.println(team);
        }
    }

    public void printLeagueName()
    {
        System.out.println(name);
    }
}
