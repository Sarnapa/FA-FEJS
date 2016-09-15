package Layout;

import DatabaseService.DatabaseConnection;

import java.awt.event.*;

public class LayoutInit{
    private LeagueView leagueView;
    private boolean asc = true;

    class LeagueChoiceListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.clearTable();
            System.out.println(leagueView.getLeagueChoiceSelected());
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), "", asc);
        }
    }

    class TableHeaderListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = leagueView.getPlayersTable().columnAtPoint(e.getPoint());
            /*String name = leagueView.getPlayersTable().getColumnName(col);
            System.out.println("Column index selected " + col + " " + name);*/
            leagueView.clearTable();
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), leagueView.getPlayersTable().getColumnName(col), asc);
            asc = !asc;
        }
    }

    private static void getPlayersFromLeague(LeagueView view, String leagueName, String orderBy, boolean asc){
        DatabaseConnection db = new DatabaseConnection();
        db.createConnection();
        db.updateView(view, leagueName, orderBy, asc);
        db.shutdown();
    }

    public LayoutInit(){
        leagueView = new LeagueView();
        leagueView.addLeagueChoiceListener(new LeagueChoiceListener());
        leagueView.addTableHeaderListener(new TableHeaderListener());
    }
}
