package Layout;

import DatabaseService.DatabaseConnection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LayoutInit{
    private LeagueView leagueView;

    class LeagueChoiceListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.clearList();
            System.out.println(leagueView.getLeagueChoiceSelected());
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected());
        }
    }

    private static void getPlayersFromLeague(LeagueView view, String leagueName){
        DatabaseConnection db = new DatabaseConnection();
        db.createConnection();
        db.updateView(view, leagueName);
        db.shutdown();
    }

    public LayoutInit(){
        leagueView = new LeagueView();
        leagueView.addLeagueChoiceListener(new LeagueChoiceListener());
    }
}
