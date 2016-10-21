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
    private String tableName; // Table's name in database
    private String url; // Team's URL
    private String name; // Team's name
    private List<String> playersUrls = new ArrayList<>(); // List of players' Urls
    private List<PlayerService> players = new ArrayList<>(); // List of players' records

    public TeamService(String leagueName, String tableName, String url)
    {
        this.leagueName = leagueName;
        this.tableName = tableName;
        this.url = url;
    }

    public void getPlayersUrls() throws InterruptedException
    {
        Document doc = getHtmlSource(url);
        if(doc != null)
        {
            Element playersContainer = doc.getElementsByClass("players-list").first();
            name = doc.getElementsByClass("left").get(1).text();
            name = name.substring(0, name.lastIndexOf('|') - 1).toUpperCase();
            Elements links = playersContainer.getElementsByTag("a");
            for (Element link : links)
            {
                if (link.parent() == playersContainer)
                        playersUrls.add(link.attr("href"));
            }
            getPlayers();
        }
    }

    private void getPlayers() throws InterruptedException
    {
        for(String url: playersUrls)
        {
            PlayerService player = new PlayerService(leagueName, tableName, name, url);
            player.getPlayerData();
            //player.printPlayerData();
            if(player.getLastName() != null)
                players.add(player);
            Thread.sleep(10);
        }
        updateDB();
    }

    public void updateDB() throws InterruptedException
    {
        DatabaseConnection database = new DatabaseConnection();
        database.createConnection();
        for(PlayerService player: players)
        {
            //player.printPlayerData();
            //database.updatePlayer(player);
            while(!database.updatePlayer(player))
            {
                Thread.sleep(10);
                //database = new DatabaseConnection();
                database.recreateConnection();
            }
        }
        database.shutdown();
    }

    public void printPlayersUrls()
    {
        for(String player: playersUrls)
        {
            System.out.println(player);
        }
    }
}
