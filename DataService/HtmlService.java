package DataService;

import DatabaseService.DatabaseConnection;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class HtmlService
{
    // To get HTML code of page
    public static Document getHtmlSource(String address) throws IOException
    {
        Connection conn = Jsoup.connect(address);
        conn.timeout(10 * 1000);
        return conn.get();
    }

    /*private static String getUrlSource(String address) throws IOException
    {
        URL url = new URL(address);
        URLConnection conn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder source = new StringBuilder(); // StringBuffer - synchronized, StringBuilder - not, so is faster
        while ((inputLine = in.readLine()) != null)
            source.append(inputLine);
        in.close();

        return source.toString();
    }*/

    /*private static void writeFromSource(String toFind, String source){
        String tmp;
        int tmpIndex;
        for (int i = -1; (i = source.indexOf(toFind, i + 1)) != -1; ) {
            tmpIndex = i+toFind.length()+2;
            tmp = source.substring(tmpIndex,source.indexOf(34,tmpIndex));
            System.out.println(tmp);
        }
    }*/
}
