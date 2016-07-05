package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LeagueService extends HtmlService
{
    private final String ROW = "row";
    private final String A = "a";
    private final String TEAMS = "league-teams-list"; // Not complete class name but it works
    private String url; // League's URL
    private List<String> teamUrls = new LinkedList<>(); // List of teams' Urls

    public LeagueService(String url)
    {
        this.url = url;
    }

    public void getTeamUrls()
    {
        try
        {
            Document doc = getHtmlSource(url);
            Elements teamsContainer = doc.getElementsByClass(TEAMS); // one element - cannot use getElementById
            Elements rows = teamsContainer.first().getElementsByClass(ROW);
            for(Element row: rows)
            {
                Elements teamsInRow = row.getElementsByTag(A);
                for (Element team: teamsInRow)
                    teamUrls.add(getUrl(team));
            }
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
    }

    public void writeTeamUrls()
    {
        for(String team: teamUrls)
        {
            System.out.println(team);
        }
    }
}
