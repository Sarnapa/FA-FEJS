package DataService;

import DatabaseService.DatabaseConnection;
import Layout.LayoutInit;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeamService
{
    private String leagueName; // League's name
    private String tableName; // Table's name in database
    private String url; // Team's URL
    private String name; // Team's name
    private List<String> playersUrls = new ArrayList<>(); // List of players' Urls
    private List<PlayerService> players = new ArrayList<>(); // List of players' records
    private LayoutInit controller;

    public TeamService(String leagueName, String tableName, String url, LayoutInit _controller)
    {
        this.leagueName = leagueName;
        this.tableName = tableName;
        this.url = url;
        this.controller = _controller;
    }

    public boolean getPlayersUrls()
    {
            boolean interrupted = false;
            Document doc = HtmlService.getHtmlSource(url, false);
            if (doc != null)
            {
                Element playersContainer = doc.getElementsByClass("players-list").first();
                name = doc.getElementsByClass("left").get(1).text();
                name = name.substring(0, name.lastIndexOf('|') - 1).toUpperCase();
                Elements links = playersContainer.getElementsByTag("a");
                for (Element link : links) {
                    /*if(Thread.currentThread().interrupted())
                    {
                        System.out.println("Interruption, BITCH");
                        throw new InterruptedException();
                    }*/
                    try
                    {
                        Thread.currentThread().sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("no kurcze 2");
                        Thread.currentThread().interrupt();
                        return true; // was interrupted
                    }
                    if (link.parent() == playersContainer)
                        playersUrls.add(link.attr("href"));
                }
                interrupted = getPlayers();
            }
            return interrupted;
    }

    private boolean getPlayers() {
        boolean interrupted = false;
        try
        {
            for (String url : playersUrls)
            {
                /*if(Thread.currentThread().interrupted())
                {
                    System.out.println("Interruption, BITCH");
                    throw new InterruptedException();
                }*/
                Thread.currentThread().sleep(10);
                PlayerService player = new PlayerService(leagueName, tableName, name, url);
                player.getPlayerData();
                //player.printPlayerData();
                if (player.getLastName() != null)
                    players.add(player);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("no kurcze 3");
            Thread.currentThread().interrupt();
            interrupted = true;
        }
        finally
        {
            updateDB();
            return interrupted;
        }
    }

    public void updateDB()
    {
        DatabaseConnection database = new DatabaseConnection();
        database.createConnection();
        for(PlayerService player: players)
        {
            //player.printPlayerData();
            //database.updatePlayer(player);
            while(!database.updatePlayer(player))
            {
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
