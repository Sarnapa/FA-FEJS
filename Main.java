import DataService.*;
import DatabaseService.DatabaseConnection;

import java.io.PrintStream;

public class Main {
    static DatabaseConnection database = new DatabaseConnection();
    public static void main(String [] args)
    {
        System.out.println("elo");
        LeaguesLinks links = new LeaguesLinks(database);
        links.getLeaguesUrls();
    }
}
