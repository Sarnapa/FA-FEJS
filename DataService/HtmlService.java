package DataService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class HtmlService
{
    private static String getUrlSource(String address) throws IOException
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
    }

    private static void writeFromSource(String toFind, String source){
        String tmp;
        int tmpIndex;
        for (int i = -1; (i = source.indexOf(toFind, i + 1)) != -1; ) {
            tmpIndex = i+toFind.length()+2;
            tmp = source.substring(tmpIndex,source.indexOf(34,tmpIndex));
            System.out.println(tmp);
        }
    }

    public static void main(String [] args)
    {
        //Scanner scanner = new Scanner(System.in);
        //String s = scanner.nextLine();
        try
        {
            String urlSource = getUrlSource("https://www.laczynaspilka.pl/klub/arka-gdynia-ssa,1550.html");
            //System.out.println(urlSource);
            writeFromSource("href",urlSource);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
