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
    private UpdateProgress progress;
    private List<Player> selectedPlayersToPdf = new ArrayList<Player>();

    /** Main window listeners **/

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
        /** Buttons listeners **/

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

    /** Update window listeners **/

    class UpdateWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            leagueView.enableView();
            updateView.dispose();
        }
    }

    class UpdateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateView.disableView();
            progress = new UpdateProgress();
            progress.addProgressListener(new ProgressListener());
            for(String s: updateView.getSelectedLeagues()){
                System.out.println(s);
            }
            startUpdate(updateView.getSelectedLeagues());
        }
    }

    /** Progress window listeners **/

    class ProgressListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            /** KILL THREADS HERE   **/
            updateView.enableView();
            progress.dispose();
        }
    }


    /** Main window functions **/

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

    /** Update window functions **/

    private void fillUpdateTable(UpdateView uv, List<String> names) {
        for (String s : names) {
            if (!s.equals("PLAYERS"))
                uv.addToLeaguesList(s);
        }
    }

    private void startUpdate(List<String> list){
        LeaguesLinks leaguesLinks = new LeaguesLinks(list, this);
        leaguesLinks.getLeaguesUrls();
    }

    /** Progress window functions **/

    public void updateTeamsCount(){
        progress.updateTeamsCount();
    }

    public void updateLeaguesCount(){
        progress.updateLeaguesCount();
    }
    /** Controller **/
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
