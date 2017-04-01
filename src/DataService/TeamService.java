package DataService;

import DatabaseService.DatabaseConnection;
import Layout.LayoutInit;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TeamService {
    private String leagueName; // League's name
    private String tableName; // Table's name in database
    private String url; // Team's URL
    private String name; // Team's name
    private List<String> playersUrls = new ArrayList<>(); // List of players' Urls
    private List<PlayerService> players = new ArrayList<>(); // List of players' records
    private LayoutInit controller;
    private InterruptionFlag interruptionFlag;

    TeamService(String leagueName, String tableName, String url, LayoutInit controller, InterruptionFlag _interruptionFlag) {
        this.leagueName = leagueName;
        this.tableName = tableName;
        this.url = url;
        this.controller = controller;
        this.interruptionFlag = _interruptionFlag;
    }

    boolean getPlayersUrls() {
        boolean interrupted = false;
        Document doc = HtmlService.getHtmlSource(url, false, controller);
        if (doc != null) {
            Element playersContainer = doc.getElementsByClass("players-list").first();
            name = doc.getElementsByClass("left").get(1).text();
            name = name.substring(0, name.lastIndexOf('|') - 1).toUpperCase();
            Elements links = playersContainer.getElementsByTag("a");
            for (Element link : links) {
                try {
                    if (interruptionFlag.getFlag())
                        throw new InterruptedException();
                } catch (InterruptedException e) {
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
        try {
            for (String url : playersUrls) {
                //System.out.println(Thread.currentThread().getId() + " " + url + " " + interruptionFlag.getFlag());
                if (interruptionFlag.getFlag())
                    throw new InterruptedException();
                PlayerService player = new PlayerService(leagueName, tableName, name, url, controller);
                player.getPlayerData();
                if (player.getLastName() != null)
                    players.add(player);
            }
            updateDB();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            updateDB();
            return true;
        }
        return false;
    }

    private void updateDB() {
        DatabaseConnection database = new DatabaseConnection(controller);
        database.createConnection();
        int failsCount = 0;
        for (PlayerService player : players) {
            int i = 0;
            while (!database.updatePlayer(player) && i < 5) {
                database.recreateConnection();
                ++i;
            }
            if (i == 5) {
                controller.log("Failed inserting row concerning player: " + player.getID() + " " + player.getFirstName() + " " + player.getLastName() + " " + player.getTeamName(), 2);
                ++failsCount;
            } else {
                controller.log("Inserted row concerning player: " + player.getID() + " " + player.getFirstName() + " " + player.getLastName() + " " + player.getTeamName(), 0);
                failsCount = 0;
            }
            if (failsCount == 5) {
                controller.log("Ended updating database due to too many errors", 1);
                break;
            }
        }
        database.shutdown();
    }

    public void printPlayersUrls() {
        for (String player : playersUrls) {
            System.out.println(player);
        }
    }
}
