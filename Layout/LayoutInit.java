package Layout;

import DatabaseService.DatabaseConnection;

import java.awt.event.*;
import java.util.List;

public class LayoutInit{
    private LeagueView leagueView;
    private boolean desc = false;
    private UpdateView updateView;

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
        }
    }


    class UpdateWindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            leagueView.enableView();
            updateView.dispose();
        }

    }

    private static void getPlayersFromLeague(LeagueView view, String leagueName, String orderBy, boolean desc){
        DatabaseConnection db = new DatabaseConnection();
        db.createConnection();
        db.updateView(view, leagueName, orderBy, desc);
        db.shutdown();
    }

    private static void getLeaguesNames(LeagueView lv){
        DatabaseConnection db = new DatabaseConnection();
        db.createConnection();
        List<String> names = db.getTablesNames();
        for(String s: names) {
            if(!s.equals("PLAYERS"))
                lv.addLeagueChoiceElement(s);
        }
        db.shutdown();
    }
    public LayoutInit(){
        leagueView = new LeagueView();
        getLeaguesNames(leagueView);
        leagueView.addLeagueChoiceListener(new LeagueChoiceListener());
        leagueView.addTableHeaderListener(new TableHeaderListener());
        leagueView.addUpdateButtonListener(new UpdateButtonListener());
    }
}
