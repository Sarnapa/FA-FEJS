package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LeagueService extends HtmlService
{
    private final String TEAMS = "league-teams-list"; // Not complete class name but it works
    private final String ROW = "row";
    private String url; // League's URL
    private List<String> teamsUrls = new LinkedList<>(); // List of teams' Urls

    public LeagueService(String url)
    {
        this.url = url;
    }

    public void getTeamsUrls()
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
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
    }

    public void writeTeamUrls()
    {
        for(String team: teamsUrls)
        {
            System.out.println(team);
        }
    }
}
