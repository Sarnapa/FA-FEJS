package DataService;

import DatabaseService.DatabaseConnection;
import org.apache.derby.database.Database;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LeagueService extends HtmlService implements Runnable
{
    private final String TEAMS = "league-teams-list"; // Not complete class name but it works
    private final String ROW = "row";
    private String name; // League's name
    private String url; // League's URL
    private List<String> teamsUrls = new LinkedList<>(); // List of teams' Urls

    public LeagueService(String name, String url, DatabaseConnection db)
    {
        this.name = name;
        this.url = url;
        this.db = db;
    }

    public void run()
    {
        try
        {
            Document doc = getHtmlSource(url);
            Element teamsContainer = doc.getElementsByClass(TEAMS).first(); // one element - cannot use getElementById
            Elements rows = teamsContainer.getElementsByClass(ROW);
            for(Element row: rows)
            {
                Elements links = row.getElementsByTag("a");
                for(Element link: links)
                    teamsUrls.add(link.attr("href"));
            }
            Thread.sleep(100);
        }
        catch (InterruptedException e)  // TODO - obsluga
        {
            e.printStackTrace();
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
        getTeams();
    }

    private void getTeams()
    {
        for(String url: teamsUrls)
        {
            TeamService team = new TeamService(name, url, db);
            team.getPlayersUrls();
        }
    }


    public void printTeamUrls()
    {
        for(String team: teamsUrls)
        {
            System.out.println(team);
        }
    }
}
