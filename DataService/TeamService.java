package DataService;

import java.util.LinkedList;
import java.util.List;

public class TeamService extends HtmlService
{
    private String url; // League's URL
    private List<String> playerUrls = new LinkedList<>(); // List of players' Urls

    public TeamService(String url)
    {
        this.url = url;
    }

    public void getPlayerUrls()
    {

    }
}
