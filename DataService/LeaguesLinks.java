package DataService;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LeaguesLinks extends HtmlService
{
    private static final String url = "https://www.laczynaspilka.pl/";
    private List<String> leagueUrls = new LinkedList<String>();

    public void getLeagueUrls()
    {
        try
        {
            Document doc = getHtmlSource(url);
            Element menu = doc.getElementsByClass("main-category").first(); // one element
            Element leaguesMenu = menu.child(5); // 5 in menu (not 4 because we have one extra tags <li>)
            Elements leagueSpans = leaguesMenu.getElementsByTag("span");
            for (Element span : leagueSpans)
            {
                String leagueName = span.text();
                Element leagueUl = span.nextElementSibling();
                String link = leagueUl.child(0).child(0).attr("href");
                switch (leagueName) {
                    case "Ekstraklasa":
                        leagueUrls.add(link);
                        break;
                    case "I Liga":
                        leagueUrls.add(link);
                        break;
                    case "II Liga":
                        leagueUrls.add(link);
                        break;
                }
            }
        }
        catch (IOException e) // TODO - obsluga
        {
            e.printStackTrace();
        }

    }
}
