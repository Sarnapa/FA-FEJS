package Layout;

import DataService.LeaguesLinks;
import DatabaseService.DatabaseConnection;
import DatabaseService.Player;
import javafx.util.Pair;

import java.awt.event.*;
import java.util.*;

import static java.lang.Thread.sleep;

public class LayoutInit{
    private LeagueView leagueView;
    private boolean desc = false;
    private UpdateView updateView;
    private UpdateProgress progress;
    private List<Player> selectedPlayersToPdf = new ArrayList<Player>();
    private ArrayList<Integer> players_ids = new ArrayList<>();
    private LeaguesLinks leaguesLinks;

    /** Main window listeners **/

    class LeagueChoiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //java.awt.EventQueue.invokeLater(new Runnable() {
                //@Override
                //public void run() {
                    leagueView.clearTable();
                    System.out.println(leagueView.getLeagueChoiceSelected());
                    getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), "", desc);

                    leagueView.disableView();
                    Map<Integer, Integer> selected = leagueView.getSelectedToPDF();
                    Iterator it = selected.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        int tmp_ind = leagueView.getRowWithValue((int)pair.getKey());
                        selected.put((int)pair.getKey(), tmp_ind);
                    }
                    leagueView.refresh();
                    leagueView.enableView();
                //}
            //});
        }
    }

    class TableHeaderListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = leagueView.getPlayersTable().columnAtPoint(e.getPoint());
            /*String name = leagueView.getPlayersTable().getColumnName(col);
            System.out.println("Column index selected " + col + " " + name);*/
            //java.awt.EventQueue.invokeLater(new Runnable() {
                //@Override
                //public void run() {
                    leagueView.clearTable();
                //}
            //});
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), leagueView.getPlayersTable().getColumnName(col), desc);
            desc = !desc;
        }
    }
        /** Buttons listeners **/

    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (LeaguesLinks.checkHostConnection())
            {
                //java.awt.EventQueue.invokeLater(new Runnable() {
                    //@Override
                    //public void run() {
                        leagueView.disableView();
                        updateView = new UpdateView();
                        updateView.addUpdateWindowListener(new UpdateWindowListener());
                        updateView.addUpdateListener(new UpdateListener());
                        fillUpdateTable(updateView, getLeaguesNames());
                    //}
                //});
            }
        }
    }

    class CreatePDFListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(players_ids.size() > 0)
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
            if(selectedPlayersToPdf.size() > 0)
            {
                PDFCreator pdfCreator = new PDFCreator(selectedPlayersToPdf);
                pdfCreator.generatePDF("pdf1");
                selectedPlayersToPdf.clear();
            }
            //java.awt.EventQueue.invokeLater(new Runnable() {
               // @Override
                //public void run() {
                    leagueView.refresh();
                //}
            //});
        }
    }

    class AddPlayersListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Map<Integer, Integer> selected = leagueView.getSelectedPlayers();
            Iterator it = selected.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry tmp = (Map.Entry)it.next();
                //System.out.println(tmp.getKey() + " " + tmp.getValue());
                if(players_ids.contains(tmp.getKey())) {    //remove
                    players_ids.remove(tmp.getKey());
                    leagueView.getSelectedToPDF().remove(tmp.getKey());
                }
                else{                                       //add
                    players_ids.add((int)tmp.getKey());
                    leagueView.getSelectedToPDF().put((int)tmp.getKey(), (int)tmp.getValue());
                }
            }
            System.out.println("----------------------\n");
            for(Integer i: players_ids){
                System.out.println(i);
            }
            //java.awt.EventQueue.invokeLater(new Runnable() {
                //@Override
                //public void run() {
                    leagueView.refresh();
                //}
           //});
        }
    }

    /** Update window listeners **/

    class UpdateWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            //java.awt.EventQueue.invokeLater(new Runnable() {
                //@Override
                //public void run() {
                    leagueView.enableView();
                    updateView.dispose();
                //}
            //});
        }
    }

    class UpdateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //java.awt.EventQueue.invokeLater(new Runnable() {
               // @Override
                //public void run() {
                    updateView.disableView();
                    progress = new UpdateProgress();
                    progress.addProgressListener(new ProgressListener());
                    for(String s: updateView.getSelectedLeagues()){
                        System.out.println(s);
                    }
                //}
            //});
            startUpdate(updateView.getSelectedLeagues());
        }
    }

    /** Progress window listeners **/

    class ProgressListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leaguesLinks.killLeagueThreads();
            //java.awt.EventQueue.invokeLater(new Runnable() {
               // @Override
                //public void run() {
                    updateView.enableView();
                    progress.dispose();
                //}
            //});
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
        leaguesLinks = new LeaguesLinks(list, this);
        leaguesLinks.getLeaguesUrls();
    }

    /** Progress window functions **/

    public void updateTeamsCount(){
        progress.updateTeamsCount();
    }

    public void updateLeaguesCount(){
        progress.updateLeaguesCount();
    }

    public void log(String s){
        progress.log(s);
    }

    /** Controller **/
    public LayoutInit()
    {
        leagueView = new LeagueView();
       // java.awt.EventQueue.invokeLater(new Runnable() {
       //     @Override
        //    public void run() {
                leagueView.disableView();
                fillLeagueChoice(leagueView, getLeaguesNames());
                leagueView.enableView();
        //    }
        //});
        leagueView.addLeagueChoiceListener(new LeagueChoiceListener());
        leagueView.addTableHeaderListener(new TableHeaderListener());
        leagueView.addUpdateButtonListener(new UpdateButtonListener());
        leagueView.addPDFButtonListener(new CreatePDFListener());
        leagueView.addPlayersButtonListener(new AddPlayersListener());
    }
}
