package Layout;

import DataService.LeaguesLinks;
import DatabaseService.DatabaseConnection;
import DatabaseService.Player;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LayoutInit{
    private LeagueView leagueView;
    private boolean desc = false;
    private UpdateView updateView;
    private List<Player> selectedPlayersToPdf = new ArrayList<Player>();

    class LeagueChoiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.clearTable();
            System.out.println(leagueView.getLeagueChoiceSelected());
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), "", desc);
        }
    }

    class TableHeaderListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = leagueView.getPlayersTable().columnAtPoint(e.getPoint());
            /*String name = leagueView.getPlayersTable().getColumnName(col);
            System.out.println("Column index selected " + col + " " + name);*/
            leagueView.clearTable();
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), leagueView.getPlayersTable().getColumnName(col), desc);
            desc = !desc;
        }
    }

    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.disableView();
            updateView = new UpdateView();
            updateView.addUpdateWindowListener(new UpdateWindowListener());
            updateView.addUpdateListener(new UpdateListener());
            fillUpdateTable(updateView, getLeaguesNames());
        }
    }


    class UpdateWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            leagueView.enableView();
            updateView.dispose();
        }

    }
    private static void fillUpdateTable(UpdateView uv, List<String> names) {
        for (String s : names) {
            if (!s.equals("PLAYERS"))
                uv.addToLeaguesList(s);
        }
    }

    class UpdateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for(String s: updateView.getSelectedLeagues()){
                System.out.println(s);
            }
            LeaguesLinks leaguesLinks = new LeaguesLinks(updateView.getSelectedLeagues());
            leaguesLinks.getLeaguesUrls();
            System.out.println("KONIEC");
        }
    }

    class CreatePDFListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(selectedPlayersToPdf.size() > 0)
            {
                PDFCreator pdfCreator = new PDFCreator(selectedPlayersToPdf);
                pdfCreator.generatePDF("pdf1");
                selectedPlayersToPdf.clear();
            }
        }
    }

    class AddPlayersListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] players_ids = leagueView.getSelectedPlayers();
            for(int i:players_ids){
                System.out.println(i);
            }
            if(players_ids.length > 0)
            {
                DatabaseConnection db = new DatabaseConnection();
                db.createConnection();
                List<String> names = db.getTablesNames();
                names.remove("PLAYERS");
                for (int i : players_ids) {
                    selectedPlayersToPdf.add(db.getDuv().getPlayerRows(i, names));
                }
                db.shutdown();
            }
        }
    }

    private static void getPlayersFromLeague(LeagueView view, String leagueName, String orderBy, boolean desc){
        DatabaseConnection db = new DatabaseConnection();
        db.createConnection();
        db.updateView(view, leagueName, orderBy, desc);
        db.shutdown();
    }

    private static List<String> getLeaguesNames(){
        DatabaseConnection db = new DatabaseConnection();
        db.createConnection();
        List<String> names = db.getTablesNames();
        db.shutdown();
        return names;
    }

    private static void fillLeagueChoice(LeagueView lv, List<String> names) {
        for (String s : names) {
            if (!s.equals("PLAYERS"))
                lv.addLeagueChoiceElement(s);
        }
    }

    public LayoutInit(){
        leagueView = new LeagueView();
        fillLeagueChoice(leagueView, getLeaguesNames());
        leagueView.addLeagueChoiceListener(new LeagueChoiceListener());
        leagueView.addTableHeaderListener(new TableHeaderListener());
        leagueView.addUpdateButtonListener(new UpdateButtonListener());
        leagueView.addPDFButtonListener(new CreatePDFListener());
        leagueView.addPlayersButtonListener(new AddPlayersListener());
    }
}
