package DataService;

import DatabaseService.DatabaseConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeamService extends HtmlService
{
    private String leagueName; // League's name
    private String url; // Team's URL
    private String name; // Team's name
    private List<String> playersUrls = new ArrayList<>(); // List of players' Urls

    public TeamService(String leagueName, String url, DatabaseConnection db)
    {
        this.leagueName = leagueName;
        this.url = url;
        this.db = db;
    }

    public void getPlayersUrls()
    {
        try
        {
            Document doc = getHtmlSource(url);
            Element playersContainer = doc.getElementsByClass("players-list").first();
            name = doc.getElementsByClass("left").get(1).text();
            name = name.substring(0, name.lastIndexOf('|')).toUpperCase();
            Elements links = playersContainer.getElementsByTag("a");
            for (Element link : links)
            {
                if (link.parent() == playersContainer)
                    playersUrls.add(link.attr("href"));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        getPlayers();
    }

    private void getPlayers()
    {
        for(String url: playersUrls)
        {
            PlayerService player = new PlayerService(leagueName, name, url);
            player.getPlayerData();
            player.printPlayerData();
            //player.insertIntoDB(db);
        }
    }

    public void printPlayersUrls()
    {
        for(String player: playersUrls)
        {
            System.out.println(player);
        }
    }
}
