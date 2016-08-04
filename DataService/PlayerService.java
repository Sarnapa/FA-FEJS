package DataService;

import DatabaseService.DatabaseConnection;
import com.sun.corba.se.impl.orb.DataCollectorBase;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.xml.crypto.Data;
import java.io.IOException;

public class PlayerService extends HtmlService
{
    static int nextID = 0;
    private String url;
    private String firstName, lastName, date, teamName, leagueName;
    private int apps, firstSquad, minutes, goals, yellowCards, redCards;

    public PlayerService(String leagueName, String teamName, String url)
    {
        this.leagueName = leagueName;
        this.teamName = teamName;
        this.url = url;
    }

    public void getPlayerData()
    {
        try
        {
            Document doc = getHtmlSource(url);
            String name = doc.getElementsByClass("header--white").first().child(0).text();
            String reportsUrl = doc.getElementsByClass("box-standard").get(3).getElementsByTag("a").attr("href");
            firstName = name.substring(0, name.lastIndexOf(' ')); // begin index - inclusive, end index - exclusive
            lastName = name.substring(name.lastIndexOf(' ') + 1, name.length());
            date = doc.getElementsByClass("light").first().nextElementSibling().text();
            getStats(reportsUrl);
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
    }

    private void getStats(String url) throws IOException
    {
        Document doc = getHtmlSource(url);
        Elements articles = doc.getElementsByClass("season__game");
        for(Element article: articles)
        {
            String leagueText = article.getElementsByClass("event").first().text();
            if(leagueText.toLowerCase().contains(leagueName.toLowerCase()))
            {
                String minutesText = article.getElementsByClass("season__game-time").first().text();
                if(!minutesText.equals(""))
                {
                    ++apps;
                }
            }
        }
    }

    public void printPlayerData()
    {
        System.out.println(firstName + " " + lastName + " " + date + " " + teamName + " " + apps);
    }

    public synchronized void insertIntoDB(DatabaseConnection database)
    {
        DatabaseConnection db_tmp = new DatabaseConnection();
        db_tmp.createConnection();
        if(!db_tmp.insertPlayer(nextID++,firstName,lastName,date))
        {
            db_tmp = new DatabaseConnection();
            db_tmp.createConnection();
            db_tmp.insertPlayer(nextID++, firstName, lastName, date);
        }
        db_tmp.shutdown();
    }
}
