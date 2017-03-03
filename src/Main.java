import DataService.PlayerService;
import DatabaseService.DatabaseConnection;
import DatabaseService.DatabaseUpdateContent;
import DatabaseService.DatabaseUpdateView;
import DatabaseService.Player;
import Layout.*;
import DataService.LeaguesLinks;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String [] args)
    {
        System.out.println("elo");
        LayoutInit layout = new LayoutInit();

        /*
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // load the driver
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        Connection conn = null; // make Derby JDBC connection
        try
        {
            conn = DriverManager.getConnection("jdbc:derby:./DB;create=true;user=fafejs;password=fafejs");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        DatabaseUpdateView duv = new DatabaseUpdateView(conn);
        List<String> names = new ArrayList<>();
        try
        {
            DatabaseMetaData metadata = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = metadata.getTables(null, null, "%", types);
            while(rs.next())
            {
                String name = rs.getString(3); // column 3 is table name
                if(!name.equals("PLAYERS"))
                    names.add(name);
            }
            rs.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        List<Player> players = new ArrayList<Player>();
        players.add(duv.getPlayerRows(766143, names));
        players.add(duv.getPlayerRows(142770, names));
        players.add(duv.getPlayerRows(668255, names));
        players.add(duv.getPlayerRows(709511, names));
        PDFCreator pdfCreator = new PDFCreator(players);
        pdfCreator.generatePDF("pdf1");
        */


        //LeaguesLinks links = new LeaguesLinks();
        //links.getLeaguesUrls();
        //links.printAllLeagues();

        //PlayerService player = new PlayerService("Ekstraklasa", "ZAGŁĘBIE LUBIN", "https://www.laczynaspilka.pl/zawodnik/maciej-dabrowski,287974.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/michal-kucharczyk,93963.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/mihail-aleksandrov,1212442.html");
        //PlayerService player = new PlayerService("Ekstraklasa","LEGIA WARSZAWA S.A.","https://www.laczynaspilka.pl/zawodnik/bartosz-bereszynski,53781.html");
        //PlayerService player = new PlayerService("TRZECIA LIGA \"GRUPA II\"","GÓRNIK KONIN","https://www.laczynaspilka.pl/wizytowka/mateusz-augustyniak,226597.html");
        //PlayerService player = new PlayerService("TRZECIA LIGA \"GRUPA II\"","SOKÓŁ KLECZEW","https://www.laczynaspilka.pl/zawodnik/marcel-koziorowski,645587.html");;
        //PlayerService player = new PlayerService("IV LIGA GRUPA 1", "CZWARTA LIGA MAZOWIECKIE GRUPA I", "GKS POGOŃ GRODZISK MAZ.","https://www.laczynaspilka.pl/zawodnik/jan-balawejder,651784.html");;
        //PlayerService player = new PlayerService("", "", "", "https://www.laczynaspilka.pl/zawodnik/rafal-glod,956472.html");
        //player.getPlayerData();
        //player.printPlayerData();
    }
}
