package DataService;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Random;

public class HtmlService
{
    // To get HTML code of page
    public static Document getHtmlSource(String address)
    {
        //address = address + "?_=ts";
        int i = 0;
        int timeout = 30 * 1000;
        Connection conn;
        Connection.Response resp = null;
        while(i < 5)
        {
            try
            {
                long startTime = System.currentTimeMillis();
                //Random generator = new Random();
                //int rand = generator.nextInt(15555);
                conn = Jsoup.connect(address).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/53.0.2785.143 Chrome/53.0.2785.143 Safari/537.36");
                conn.referrer("http://www.google.com");
                //conn = Jsoup.connect(address);
                conn.timeout(timeout).ignoreHttpErrors(true);
                resp = conn.execute();
                if (resp.statusCode() == 200)
                {
                    System.out.print(Thread.currentThread().getId() + " " + address + " ");
                    System.out.println(System.currentTimeMillis() - startTime);
                    return conn.get();
                }
                ++i;
            }
            catch (SocketTimeoutException ste)
            {
                ++i;
                System.out.println("Nie można pobrać danych z adresu: " + address + " Powód: " + ste.getMessage() + " Próba: " + i);
                ste.printStackTrace();
            }
            catch (IOException e)
            {
                System.out.println("kupka");
                //System.out.println("Nie można pobrać danych z adresu: " + address + " Powód: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        //finally
        //{
            if (resp != null)
                System.out.println("Nie można pobrać danych z adresu: " + address + " Kod HTML:  " + resp.statusCode() + " Wiadomość: " + resp.statusMessage());
            return null;
        //}
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
