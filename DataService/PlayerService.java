package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerService extends HtmlService
{
    static int nextID = 0;
    private String url;
    private int ID;
    private String firstName, lastName, teamName, leagueName;
    private Date date;
    private int apps, firstSquad, minutes, goals, yellowCards, redCards;

    public int getID()
    {
        return ID;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public String getLeagueName()
    {
        return leagueName;
    }

    public Date getDate()
    {
        return date;
    }

    public int getApps()
    {
        return apps;
    }

    public int getFirstSquad()
    {
        return firstSquad;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public int getGoals()
    {
        return goals;
    }

    public int getYellowCards()
    {
        return yellowCards;
    }

    public int getRedCards()
    {
        return redCards;
    }

    public PlayerService(String leagueName, String teamName, String url)
    {
        this.leagueName = leagueName;
        this.teamName = teamName;
        this.url = url;
        ID = Integer.parseInt(url.substring(url.lastIndexOf(',') + 1,url.lastIndexOf('.')));
    }

    public void getPlayerData()
    {
        try
        {
            Document doc = getHtmlSource(url);
            String name = doc.getElementsByClass("header--white").first().child(0).text();
            String reportsUrl = doc.getElementsByClass("box-standard").get(3).getElementsByTag("a").attr("href");
            firstName = name.substring(0, name.indexOf(' ')).toLowerCase(); // begin index - inclusive, end index - exclusive
            firstName = firstName.substring(0,1).toUpperCase() + firstName.substring(1);
            lastName = name.substring(name.indexOf(' ') + 1, name.length()).toLowerCase();
            lastName = lastName.substring(0,1).toUpperCase() + lastName.substring(1);
            String dateText = doc.getElementsByClass("light").first().nextElementSibling().text();
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            date = format.parse(dateText);
            getStats(name, reportsUrl);
        }
        catch (ParseException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
        catch (NullPointerException e) // GORNIK KONIN SYNDROME - only player's name on website
        {
            try
            {
                Document doc = getHtmlSource(url);
                String name = doc.getElementsByClass("cf").get(6).child(0).text();
                name = name.substring(0, name.lastIndexOf('|') - 1); // - 1 because of 1 space
                firstName = name.substring(0, name.lastIndexOf(' ')); // begin index - inclusive, end index - exclusive
                lastName = name.substring(name.lastIndexOf(' ') + 1, name.length());
            }
            catch(IOException IOe) // TODO - obsluga
            {
                IOe.printStackTrace();
            }
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }
    }

    private void getStats(String name, String url) throws IOException
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
                    minutesText = minutesText.substring(7);
                    int begin = Integer.parseInt(minutesText.substring(0, minutesText.lastIndexOf('-')));
                    int end = Integer.parseInt(minutesText.substring(minutesText.lastIndexOf('-') + 1, minutesText.length()));
                    if(begin == 0)
                        ++firstSquad;
                    ++apps;
                    minutes = minutes + (end - begin);
                    Elements infoDivs = article.getElementsByClass("info");
                    for(Element div: infoDivs)
                    {
                        if(div.text().contains(name))
                        {
                            String className = div.child(0).className();
                            switch(className)
                            {
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

    public void printPlayerData()
    {
        System.out.println(ID + " " + firstName + " " + lastName + " " + date + " " + teamName + " " + apps + " " +
                firstSquad + " " + minutes + " " + goals + " " + yellowCards + " " + redCards);
    }
}
