package DataService;

import DatabaseService.DatabaseConnection;
import com.sun.corba.se.impl.orb.DataCollectorBase;
import org.jsoup.nodes.Document;
import javax.xml.crypto.Data;
import java.io.IOException;

public class PlayerService extends HtmlService
{
    static int nextID = 2;
    private String url;
    private String firstName;
    private String lastName;
    private String date;

    public PlayerService(String url)
    {
        this.url = url;
    }

    public void getPlayerData()
    {
        try
        {
            Document doc = getHtmlSource(url);
            String name = doc.getElementsByClass("header--white").first().child(0).text();
            firstName = name.substring(0, name.lastIndexOf(' ')); // begin index - inclusive, end index - exclusive
            lastName = name.substring(name.lastIndexOf(' ') + 1, name.length());
            date = doc.getElementsByClass("light").first().nextElementSibling().text();
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
    }

    public void printPlayerData()
    {
        System.out.println(firstName + " " + lastName + " " + date);
    }
    public synchronized void insertIntoDB(DatabaseConnection database)
    {
        database.insertPlayer(nextID++,firstName,lastName,date);
    }
}
