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
    private String url; // League's URL
    private List<String> playersUrls = new ArrayList<>(); // List of players' Urls

    public TeamService(String url, DatabaseConnection db)
    {
        this.url = url;
        this.db = db;
    }

    public void getPlayersUrls()
    {
        try {
            Document doc = getHtmlSource(url);
            Element playersContainer = doc.getElementsByClass("players-list").first();
            Elements links = playersContainer.getElementsByTag("a");
            for (Element link : links) {
                if (link.parent() == playersContainer)
                    playersUrls.add(link.attr("href"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getPlayers();
        //printPlayersUrls();
    }

    private void getPlayers()
    {
        for(String url: playersUrls)
        {
            PlayerService player = new PlayerService(url);
            player.getPlayerData();
            //player.printPlayerData();
            player.insertIntoDB(db);
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
