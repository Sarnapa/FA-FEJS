package DataService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class HtmlService
{
    private static String getUrlSource(String url) throws IOException
    {
        URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();
    }

    public static void main(String [] args)
    {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        try
        {
            String urlSource = getUrlSource(s);
            System.out.println(urlSource);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
