package DataService;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class HtmlService
{
    // To get HTML code of page
    public static Document getHtmlSource(String address)
    {
        int i = 0;
        while(i < 10)
        {
            try
            {
                Connection conn = Jsoup.connect(address);
                conn.timeout(10 * 1000);
                Connection.Response resp = conn.execute();
                if (resp.statusCode() == 200)
                    return conn.get();
                ++i;
            }
            catch (IOException e)
            {
                ++i;
                if(i == 10)
                {
                    System.out.println("Nie można pobrać danych z adresu: " + address + " Powód: " + e.getMessage());
                    return null;
                }
            }
        }
        System.out.println("Nie można pobrać danych z adresu: " + address + " Powód: Inny HTML Status niż 200");
        return null;
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
