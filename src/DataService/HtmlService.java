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
        Connection conn;
        Connection.Response resp = null;
        while(i < 10)
        {
            try
            {
                long startTime = System.currentTimeMillis();
                conn = Jsoup.connect(address);
                conn.timeout(10 * 1000).ignoreHttpErrors(true);
                resp = conn.execute();
                if (resp.statusCode() == 200)
                {
                    System.out.print(address + " ");
                    System.out.println(System.currentTimeMillis() - startTime);
                    return conn.get();
                }
                ++i;
            }
            catch (IOException e)
            {
                System.out.println("kupka");
                //System.out.println("Nie można pobrać danych z adresu: " + address + " Powód: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        if(resp != null)
            System.out.println("Nie można pobrać danych z adresu: " + address + "Kod HTML:  " + resp.statusCode() + " Wiadomość: " + resp.statusMessage());
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
