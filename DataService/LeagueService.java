package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LeagueService extends HtmlService implements Runnable
{
    private String name; // League's name
    private String url; // League's URL
    private List<String> teamsUrls = new LinkedList<>(); // List of teams' Urls

    public LeagueService(String url)
    {
        this.url = url;
    }

    public void run()
    {
        try
        {
            Document doc = getHtmlSource(url);
            name = doc.getElementsByClass("show-drop").first().text();
            Element teamsContainer = doc.getElementsByClass("league-teams-list").first(); // one element - cannot use getElementById. Not complete class name but it works
            Elements rows = teamsContainer.getElementsByClass("row");
            for(Element row: rows)
            {
                Elements links = row.getElementsByTag("a");
                for(Element link: links)
                    teamsUrls.add(link.attr("href"));
            }
            getTeams();
        }
        /*catch (InterruptedException e)  // TODO - obsluga
        {
            e.printStackTrace();
        }*/
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
    }

    private void getTeams()
    {
        for(String url: teamsUrls)
        {
            TeamService team = new TeamService(name, url);
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

    public void printLeagueName()
    {
        System.out.println(name);
    }
}
