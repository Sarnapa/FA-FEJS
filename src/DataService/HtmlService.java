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
    public static Document getHtmlSource(String address, boolean isJSON)
    {
        //address = address + "?_=ts";
        int timeout = 30 * 1000;
        int delay1 = 5 * 1000;
        int delay2 = 10 * 1000;
            if(isJSON)
                return loadJSON(address, timeout, delay2);
            else
                return loadHTML(address, timeout, delay1);
    }


    private static Document loadJSON(String address, int timeout, int delay)
    {
        int i = 0;
        Connection conn;
        Connection.Response resp = null;
        while(i < 5)
        {
            try
            {
                long startTime = System.currentTimeMillis();
                conn = Jsoup.connect(address)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/53.0.2785.143 Chrome/53.0.2785.143 Safari/537.36")
                        .maxBodySize(0).referrer("http://www.google.com")
                        .timeout(timeout).ignoreHttpErrors(true).ignoreContentType(true);
                resp = conn.execute();
                if (resp.statusCode() == 200)
                {
                    System.out.print(Thread.currentThread().getId() + " " + address + " ");
                    System.out.println(System.currentTimeMillis() - startTime);
                    String json = resp.body();
                    Document doc = Jsoup.parse(json);
                    return doc;
                }
                else if (resp.statusCode() == 420) // ( ͡° ͜ʖ ͡°)
                {
                    try
                    {
                        System.out.println("420_z_JSON: " + Thread.currentThread().getId() + " " + address + " ");
                        Thread.currentThread().sleep(delay);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
                else
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
        if (resp != null)
            System.out.println("Nie można pobrać danych z adresu: " + address + " Kod HTML:  " + resp.statusCode() + " Wiadomość: " + resp.statusMessage());
        return null;
    }

    private static Document loadHTML(String address, int timeout, int delay)
    {
        int i = 0;
        Connection conn;
        Connection.Response resp = null;
        while(i < 5)
        {
            try
            {
                long startTime = System.currentTimeMillis();
                conn = Jsoup.connect(address)
                        //.userAgent("Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.10136")
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/53.0.2785.143 Chrome/53.0.2785.143 Safari/537.36")
                        .maxBodySize(0).referrer("http://www.google.com")
                        .timeout(timeout).ignoreHttpErrors(true);
                resp = conn.execute();
                if (resp.statusCode() == 200)
                {
                    System.out.print(Thread.currentThread().getId() + " " + address + " ");
                    System.out.println(System.currentTimeMillis() - startTime);
                    return conn.get();
                }
                else if (resp.statusCode() == 420) // ( ͡° ͜ʖ ͡°)
                {
                    try
                    {
                        System.out.println("420_bez_JSON: " + Thread.currentThread().getId() + " " + address + " ");
                        Thread.currentThread().sleep(delay);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
                else
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
                e.printStackTrace();
                return null;
            }
        }
        if (resp != null)
            System.out.println("Nie można pobrać danych z adresu: " + address + " Kod HTML:  " + resp.statusCode() + " Wiadomość: " + resp.statusMessage());
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
