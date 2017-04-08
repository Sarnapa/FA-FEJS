package Layout;

import DataService.FolderContentReader;
import DataService.LeaguesLinks;
import DatabaseService.DatabaseConnection;
import DatabaseService.Player;
import org.apache.derby.database.Database;
import org.jdatepicker.impl.JDatePickerImpl;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LayoutInit {
    private LeagueView leagueView;
    private boolean desc = false;
    private UpdateView updateView;
    private UpdateProgress progress;
    private InsertModeWindow insertModeWindow;
    private List<Player> selectedPlayersToPdf = new ArrayList<>();
    private HashMap<Integer, Integer> playersIDs = new HashMap<>();
    private LeaguesLinks leaguesLinks;
    private final static String pdfsFolderDst = "./pdfs";
    private String currentLeague;

    /**
     * Main window listeners
     **/

    class LeagueChoiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.clearTable();
            //System.out.println(leagueView.getLeagueChoiceSelected());
            currentLeague = leagueView.getLeagueChoiceSelected();
            System.out.println(currentLeague);
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

    class TableListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel) e.getSource();
            String columnName = model.getColumnName(column);
            int id = (int) model.getValueAt(row, 0);
            Object newValue = model.getValueAt(row, column);
            String team = (String) model.getValueAt(row, 4);
            switch (column) {
                case 1:
                case 2:
                case 4:
                    newValue = newValue.toString();
                    break;
                case 3:
                    DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                    try {
                        java.util.Date tmp = format.parse(newValue.toString());
                        java.sql.Date tmp2 = new java.sql.Date(tmp.getTime());
                        newValue = tmp2;
                        System.out.println(newValue);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    break;
                default:
                    newValue = Integer.parseInt(newValue.toString());
                    break;

            }
            System.out.println(row + " " + column + " " + columnName + " :" + id + " - " + newValue + " " + newValue.getClass());
            if (column > 3) {
                updateDatabase(id, team, currentLeague, columnName, newValue);
            } else {
                updateDatabase(id, "", "", columnName, newValue);
            }


        }
    }

    /**
     * Buttons listeners
     **/


    class EditModeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (leagueView.editMode())
                leagueView.addTableModelListener(new TableListener());
            else
                leagueView.removeTableModelListener();
        }
    }

    class InsertModeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.disableView();
            insertModeWindow = new InsertModeWindow();
            insertModeWindow.addInsertButtonListener(new InsertButtonListener());
            insertModeWindow.addInsertModeWindowListener(new InsertModeWindowListener());
        }
    }

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
            if(selectedPlayersToPdf.size() > 0) {
                PDFCreator pdfCreator = new PDFCreator(selectedPlayersToPdf);
                boolean isReady = false;
                FolderContentReader contentReader = new FolderContentReader(pdfsFolderDst);
                String pdfName = "";
                while(!isReady)
                {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH.mm.ss");
                    LocalDateTime now = LocalDateTime.now();
                    String initString = dtf.format(now);
                    Object resultInput = JOptionPane.showInputDialog(
                            leagueView,
                            "Write PDF filename.",
                            "FA-FEJS",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            initString);
                    if(resultInput != null)
                    {
                        pdfName = (String) resultInput;
                        if (!pdfName.equals("")) {
                            if (contentReader.isFile(pdfName + ".pdf")) {
                                Object[] options = {"Yes", "No"};
                                int optionCode = JOptionPane.showOptionDialog(leagueView, "Do you want to overwrite it?", "File with this name already existed", JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                                if (optionCode == 0)
                                    isReady = true;
                            } else
                                isReady = true;
                        }
                        else
                            break;
                    }
                    else
                        break;
                }
                if(isReady)
                {
                    String dest = "./pdfs/" + pdfName + ".pdf";
                    pdfCreator.generatePDF(dest);
                    playersIDs.clear();
                }
                selectedPlayersToPdf.clear();
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
     * Insert mode window listeners
     **/
    class InsertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("elo");
        }
    }

    class InsertModeWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            leagueView.enableView();
        }
    }

    /**
     * Main window functions
     **/
    private void updateDatabase(int id, String team, String leagueName, String columnName, Object newValue) {
        System.out.println("elo");
        DatabaseConnection db = new DatabaseConnection(this);
        db.createConnection();
        db.updatePlayersSpecificColumn(id, team, leagueName, columnName, newValue);
        db.shutdown();
    }

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
        leagueView.addEditModeButtonListener(new EditModeButtonListener());
        leagueView.addTableHeaderListener(new TableHeaderListener());
        leagueView.addUpdateButtonListener(new UpdateButtonListener());
        leagueView.addPDFButtonListener(new CreatePDFListener());
        leagueView.addPlayersButtonListener(new AddPlayersListener());
        leagueView.addInsertModeButtonListener(new InsertModeButtonListener());

        leaguesLinks = new LeaguesLinks(this);
    }

    public HashMap<Integer, Integer> getPlayersIDs() {
        return playersIDs;
    }
}
