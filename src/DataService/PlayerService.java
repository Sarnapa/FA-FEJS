package DataService;

import Layout.LayoutInit;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The last level of getting players data task.
 * This time we collect data connected with player - personal data and stats.
 */

public class PlayerService {
    private String url;
    private int ID;
    private String firstName, lastName, teamName, leagueName, tableName;
    private Date date;
    private int apps, firstSquad, minutes, goals, yellowCards, redCards;
    private LayoutInit controller;

    public int getID() {
        return ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getTableName() {
        return tableName;
    }

    public Date getDate() {
        return date;
    }

    public int getApps() {
        return apps;
    }

    public int getFirstSquad() {
        return firstSquad;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getGoals() {
        return goals;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    PlayerService(String leagueName, String tableName, String teamName, String url, LayoutInit controller) {
        this.leagueName = leagueName;
        this.tableName = tableName;
        this.teamName = teamName;
        this.url = url;
        ID = Integer.parseInt(url.substring(url.lastIndexOf(',') + 1, url.lastIndexOf('.')));
        this.controller = controller;
    }

    /**
     * First phase of getting player data.
     * There are different structures for players pages - therefore, this function is quite expanded.
     */

    void getPlayerData() throws InterruptedException {
        Document doc = HtmlService.getHtmlSource(url, false, controller);
        try {
            if (doc != null) {
                String name = doc.getElementsByClass("header--white").first().child(0).text();
                String reportsUrl = doc.getElementsByClass("box-standard").get(3).getElementsByTag("a").attr("href");
                firstName = name.substring(0, name.indexOf(' ')).toLowerCase(); // begin index - inclusive, end index - exclusive
                firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                lastName = name.substring(name.indexOf(' ') + 1, name.length()).toLowerCase();
                lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                String dateText = doc.getElementsByClass("light").first().nextElementSibling().text();
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                date = format.parse(dateText);
                getStats(name, reportsUrl);
                controller.log("Downloaded data concerning player: " + ID + " " + firstName + " " + lastName + " " + teamName, 0);
            }
        } catch (ParseException e) {
            controller.log("Cannot parse text contained birthdate of player from address: " + url, 2);
        } catch (NullPointerException e) // GORNIK KONIN SYNDROME - only player's name on website or AKADEMIA MLODYCH ORLOW SYNDROME
        {
            if (doc != null) {
                String name = doc.getElementsByClass("cf").get(6).child(0).text();
                if (!name.substring(0, name.indexOf(' ')).equals("Profil")) // 1 case - GORNIK KONIN SYNDROME, 2 case - AKADEMIA MLODYCH ORLOW SYNDROME
                {
                    if (name.lastIndexOf('|') >= 0) {
                        name = name.substring(0, name.lastIndexOf('|') - 1); // - 1 because of 1 space
                        firstName = name.substring(0, name.lastIndexOf(' ')); // begin index - inclusive, end index - exclusive
                        lastName = name.substring(name.lastIndexOf(' ') + 1, name.length());
                    }
                } else {
                    name = doc.getElementsByClass("head-black").first().text();
                    firstName = name.substring(0, name.lastIndexOf(' ')); // begin index - inclusive, end index - exclusive
                    lastName = name.substring(name.lastIndexOf(' ') + 1, name.length());
                }
                controller.log("Downloaded data concerning player: " + ID + " " + firstName + " " + lastName + " " + teamName, 0);
            }
        }
    }

    /**
     * Downloading player stats - it is based on parsing player page with stats.
     */

    private void getStats(String name, String url) {
        Document doc = HtmlService.getHtmlSource(url, false, controller);
        if (doc != null) {
            Elements articles = doc.getElementsByClass("season__game");
            for (Element article : articles) {
                String leagueText = article.getElementsByClass("event").first().text();
                Element teamNamesContainer = article.getElementsByClass("teams").first();
                Elements teamNameTexts = teamNamesContainer.getElementsByTag("a");
                boolean isCorrectTeam = false;
                for (int i = 0; i < teamNameTexts.size(); ++i) {
                    if (teamNameTexts.get(i).text().toLowerCase().equals(teamName.toLowerCase())) {
                        isCorrectTeam = true;
                        break;
                    }
                }
                if (leagueText.toLowerCase().contains(leagueName.toLowerCase()) && isCorrectTeam) {
                    String minutesText = article.getElementsByClass("season__game-time").first().text();
                    if (!minutesText.equals("")) {
                        minutesText = minutesText.substring(7);
                        int begin = Integer.parseInt(minutesText.substring(0, minutesText.lastIndexOf('-')));
                        int end = Integer.parseInt(minutesText.substring(minutesText.lastIndexOf('-') + 1, minutesText.length()));
                        if (begin == 0)
                            ++firstSquad;
                        ++apps;
                        minutes = minutes + (end - begin);
                        Elements infoDivs = article.getElementsByClass("info");
                        for (Element div : infoDivs) {
                            if (div.text().contains(name)) {
                                String className = div.child(0).className();
                                switch (className) {
                                    case "i-goal-small":
                                        ++goals;
                                        break;
                                    case "i-card-yellow card--small":
                                        ++yellowCards;
                                        break;
                                    case "i-card-red card--small":
                                        ++redCards;
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Print section - for debugging
     */

    public void printPlayerData() {
        if (firstName != null)
            System.out.println(Thread.currentThread().getId() + " " + ID + " " + firstName + " " + lastName + " " + date + " " + teamName + " " + apps + " " +
                    firstSquad + " " + minutes + " " + goals + " " + yellowCards + " " + redCards);
        else
            System.out.println("ALERT: " + Thread.currentThread().getId() + " " + ID + " " + firstName + " " + lastName + " " + date + " " + teamName);
    }
}
