package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LeagueService extends HtmlService
{
    private final String TABLE = "table-template"; // Not complete class name but it works
    private final String ROW = "row-link";
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
            Elements teamsInTable = doc.getElementsByClass(TABLE); // one element - cannot use getElementById
            Elements rows = teamsInTable.first().getElementsByClass(ROW);
            for(Element row: rows)
            {
                teamUrls.add(row.attr("data-url"));
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
