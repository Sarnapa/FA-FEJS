package Layout;

import DataService.LeaguesLinks;
import DatabaseService.DatabaseConnection;
import DatabaseService.Player;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class LayoutInit {
    private LeagueView leagueView;
    private boolean desc = false;
    private UpdateView updateView;
    private UpdateProgress progress;
    private List<Player> selectedPlayersToPdf = new ArrayList<>();
    private HashMap<Integer, Integer> playersIDs = new HashMap<>();
    private LeaguesLinks leaguesLinks;

    /**
     * Main window listeners
     **/

    class LeagueChoiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.clearTable();
            System.out.println(leagueView.getLeagueChoiceSelected());
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), "", desc);

            leagueView.disableView();
            Iterator it = playersIDs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                int tmp_ind = leagueView.getRowWithValue((int) pair.getKey());
                playersIDs.put((int) pair.getKey(), tmp_ind);
            }
            leagueView.refresh();
            leagueView.enableView();
        }
    }

    class TableHeaderListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = leagueView.getPlayersTable().columnAtPoint(e.getPoint());
            leagueView.clearTable();
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), leagueView.getPlayersTable().getColumnName(col), desc);
            desc = !desc;
        }
    }

    /**
     * Buttons listeners
     **/

    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (leaguesLinks.checkHostConnection()) {
                leagueView.disableUpdateButton();
                updateView = new UpdateView();
                updateView.addUpdateWindowListener(new UpdateWindowListener());
                updateView.addUpdateListener(new UpdateListener());
                fillUpdateTable(updateView, getLeaguesNames());
            }
        }
    }

    class CreatePDFListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (playersIDs.size() > 0) {
                DatabaseConnection db = new DatabaseConnection(LayoutInit.this);
                db.createConnection();
                List<String> names = db.getTablesNames();
                names.remove("PLAYERS");
                Iterator it = playersIDs.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry tmp = (Map.Entry) it.next();
                    selectedPlayersToPdf.add(db.getDuv().getPlayerRows((int) tmp.getKey(), names));
                }
                db.shutdown();
            }
            if (selectedPlayersToPdf.size() > 0) {
                PDFCreator pdfCreator = new PDFCreator(selectedPlayersToPdf);
                String pdfName = (String) JOptionPane.showInputDialog(
                        leagueView,
                        "Write PDF filename.",
                        "FA-FEJS",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
                pdfCreator.generatePDF(pdfName);
                selectedPlayersToPdf.clear();
                playersIDs.clear();
            }
            leagueView.refresh();
        }
    }

    class AddPlayersListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Map<Integer, Integer> selected = leagueView.getSelectedPlayers();
            Iterator it = selected.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry tmp = (Map.Entry) it.next();
                if (playersIDs.containsKey(tmp.getKey())) {    //remove
                    playersIDs.remove(tmp.getKey());
                } else {                                       //add
                    playersIDs.put((int) tmp.getKey(), (int) tmp.getValue());
                }
            }
            leagueView.refresh();
        }
    }

    /**
     * Update window listeners
     **/

    class UpdateWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            leagueView.enableUpdateButton();
        }
    }

    class UpdateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> selectedLeagues = updateView.getSelectedLeagues();
            if (selectedLeagues.size() > 0) {
                updateView.disableView();
                progress = new UpdateProgress();
                progress.addProgressListener(new ProgressListener());
                startUpdate(updateView.getSelectedLeagues());
            }
        }
    }

    /**
     * Progress window listeners
     **/


    class ProgressListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            progress.disableUpdateButton();
            SwingWorker myWorker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    log("Finishing work. Please wait...", 1);
                    leaguesLinks.killLeagueThreads();
                    leaguesLinks.clear();
                    updateView.enableView();
                    log("Data update completed! You can now close this window.", 1);
                    progress.changeCloseOperation();
                    return null;
                }
            };
            myWorker.execute();

        }
    }

    /**
     * Main window functions
     **/

    private void getPlayersFromLeague(LeagueView view, String leagueName, String orderBy, boolean desc) {
        DatabaseConnection db = new DatabaseConnection(this);
        db.createConnection();
        db.updateView(view, leagueName, orderBy, desc);
        db.shutdown();
    }

    private List<String> getLeaguesNames() {
        DatabaseConnection db = new DatabaseConnection(this);
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

    /**
     * Update window functions
     **/

    private void fillUpdateTable(UpdateView uv, List<String> names) {
        for (String s : names) {
            if (!s.equals("PLAYERS"))
                uv.addToLeaguesList(s);
        }
    }

    private void startUpdate(List<String> list) {
        leaguesLinks.setSelectedLeagues(list);
        Thread mainUpdateThread = new Thread(leaguesLinks);
        mainUpdateThread.start();
    }

    /**
     * Progress window functions
     **/

    public void updateTeamsCount() {
        progress.updateTeamsCount();
    }

    public void updateLeaguesCount() {
        progress.updateLeaguesCount();
    }

    public void log(String s, int c) {
        progress.log(s, c);
    }

    /**
     * Message Dialog function
     **/

    public void showDialog(String dialogTitle, String text, int dialogOption, int parentID) {
        //JOptionPane.ERROR_MESSAGE = 0, JOptionPane.INFORMATION_MESSAGE = 1, JOptionPane.WARNING_MESSAGE = 2
        switch (parentID) {
            case 0:
                JOptionPane.showMessageDialog(leagueView, text, dialogTitle, dialogOption);
                break;
            case 1:
                JOptionPane.showMessageDialog(updateView, text, dialogTitle, dialogOption);
                break;
            case 2:
                JOptionPane.showMessageDialog(progress, text, dialogTitle, dialogOption);
                break;
            default:
                JOptionPane.showMessageDialog(null, text, dialogTitle, dialogOption);
                break;
        }
    }

    /**
     * Controller
     **/
    public LayoutInit() {
        leagueView = new LeagueView(this);
        leagueView.disableView();
        fillLeagueChoice(leagueView, getLeaguesNames());
        leagueView.enableView();
        leagueView.addLeagueChoiceListener(new LeagueChoiceListener());
        leagueView.addTableHeaderListener(new TableHeaderListener());
        leagueView.addUpdateButtonListener(new UpdateButtonListener());
        leagueView.addPDFButtonListener(new CreatePDFListener());
        leagueView.addPlayersButtonListener(new AddPlayersListener());
        leaguesLinks = new LeaguesLinks(this);
    }

    public HashMap<Integer, Integer> getPlayersIDs() {
        return playersIDs;
    }
}
