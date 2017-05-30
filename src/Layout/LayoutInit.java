package Layout;

import DataService.FolderContentReader;
import DataService.LeaguesLinks;
import DatabaseService.DatabaseConnection;
import DatabaseService.Player;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
    private InsertModeInputVerifier verifier;
    private boolean isUpdateCancelled = false;

    /**
     * Main window listeners
     **/

    class LeagueChoiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.clearTable();
            currentLeague = leagueView.getLeagueChoiceSelected();
            getPlayersFromLeague(leagueView, leagueView.getLeagueChoiceSelected(), "", desc);
            leagueView.disableView();
            Iterator it = playersIDs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                int tmp_ind = leagueView.getRowWithValue((int) pair.getKey());
                playersIDs.put((int) pair.getKey(), tmp_ind);
            }
            leagueView.setEnabledModeButtons();
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

    class TableCellAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tableCellListener = (TableCellListener) e.getSource();
            TableModel model = tableCellListener.getTable().getModel();
            int row = tableCellListener.getRow();
            int column = tableCellListener.getColumn();
            String columnName = model.getColumnName(column);
            int id = (int) model.getValueAt(row, 0);
            Object newValue = tableCellListener.getNewValue();
            String team = (String) model.getValueAt(row, 4);
            boolean isCorrect = false;
            ValueValidator validator = new ValueValidator();
            if (validator.isEmpty(newValue))
            {
                model.setValueAt(tableCellListener.getOldValue(), row, column);
                showDialog("Wrong value!", "Cannot insert empty value.", 0, 0);
            }
            else
            {
                switch (column) {
                    case 1:
                        if (validator.findSpecialCharacter(newValue.toString())) {
                            model.setValueAt(tableCellListener.getOldValue(), row, column);
                            showDialog("Wrong value!", "Detected special characters in inserted text.", 0, 0);
                        } else
                            isCorrect = true;
                        break;
                    case 2:
                        if (validator.findSpecialCharacter(newValue.toString())) {
                            model.setValueAt(tableCellListener.getOldValue(), row, column);
                            showDialog("Wrong value!", "Detected special characters in inserted text.", 0, 0);
                        } else
                            isCorrect = true;
                        break;
                    case 3:
                        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                        try {
                            String dateString = newValue.toString();
                            //System.out.println("DateString: " + dateString);
                            if (validator.isValidDate(newValue.toString()))
                            {
                                java.util.Date tmp = format.parse(dateString);
                                newValue = new java.sql.Date(tmp.getTime());
                                //System.out.println(newValue);
                                isCorrect = true;
                            }
                            else
                            {
                                model.setValueAt(tableCellListener.getOldValue(), row, column);
                                showDialog("Wrong value!", "Wrong date format.", 0, 0);
                            }
                        }
                        catch (ParseException pe) {
                            model.setValueAt(tableCellListener.getOldValue(), row, column);
                            showDialog("Wrong value!", "Wrong date format.", 0, 0);
                        }
                        break;
                    default:
                        try {
                            newValue = Integer.parseInt(newValue.toString());
                            isCorrect = true;
                        }
                        catch (NumberFormatException nfe) {
                            model.setValueAt(tableCellListener.getOldValue(), row, column);
                            showDialog("Wrong value!", "Detected letters or special characters in inserted text.", 0, 0);
                        }
                        break;
                }
                if (isCorrect)
                {
                    if (column > 3) {
                        updateDatabase(id, team, currentLeague, columnName, newValue);
                    } else {
                        updateDatabase(id, "", "", columnName, newValue);
                    }
                }
            }
        }
    }

    /**
     * Buttons listeners
     **/

    class EditModeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(leagueView.editMode())
                leagueView.createTableCellListener(new TableCellAction());
            else
                leagueView.removeTableCellListener();
        }
    }

    class DelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingWorker myWorker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    Map<Pair<Integer, String>, Integer> selected = leagueView.getSelectedPlayers();
                    Iterator it = selected.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry tmp = (Map.Entry) it.next();
                        Pair tmppair = (Pair)tmp.getKey();
                        if(delFromDatabase((int)tmppair.getKey(), (String)tmppair.getValue())){
                            leagueView.delFromTable((int)tmppair.getKey());
                            leagueView.refresh();
                        }
                        else log("Error while deleting from database.", 2);
                    }
                    return null;
                }
            };
            myWorker.execute();
        }
    }

    class InsertModeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            leagueView.disableView();
            verifier = new InsertModeInputVerifier(new ValueValidator());
            insertModeWindow = new InsertModeWindow(verifier);
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
                DatabaseConnection db = new DatabaseConnection();
                if(!db.createConnection())
                    showDialog("Database Error", "Cannot connect to database.", 0, 0);
                List<String> names = db.getTablesNames();
                if(names != null)
                    names.remove("PLAYERS");
                else
                {
                    showDialog("Database Error", "Cannot get tables names.", 0, 0);
                }
                Iterator it = playersIDs.entrySet().iterator();
                while (it.hasNext())
                {
                    Map.Entry tmp = (Map.Entry) it.next();
                    try
                    {
                        selectedPlayersToPdf.add(db.getDuv().getPlayerRows((int) tmp.getKey(), names));
                    }
                    catch(SQLException sqlE)
                    {
                        showDialog("Database Error", "Cannot get player rows from database. Player ID: " + (int) tmp.getKey(), 0, 0);
                    }
                }
                if(!db.shutdown())
                    showDialog("Database Error", "Cannot shutdown database.", 0, 0);
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
                    try
                    {
                        pdfCreator.generatePDF(dest);
                    }
                    catch (FileNotFoundException e1)
                    {
                        showDialog("PDF Creator Error", "Not found main directory for PDF files.", 0, 0);
                    }
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
            Map<Pair<Integer, String>, Integer> selected = leagueView.getSelectedPlayers();
            Iterator it = selected.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry tmp = (Map.Entry) it.next();
                Pair tmppair = (Pair)tmp.getKey();
                if (playersIDs.containsKey(tmppair.getKey())) {    //remove
                    playersIDs.remove(tmppair.getKey());
                } else {                                       //add
                    playersIDs.put((int) tmppair.getKey(), (int) tmp.getValue());
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
                progress.addProgressListener(new ProgressCancelButtonListener());
                startUpdate(updateView.getSelectedLeagues());
            }
        }
    }

    /**
     * Progress window listeners
     **/

    class ProgressCancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            isUpdateCancelled = true;
            progress.disableStopUpdateButton();
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
            if(verifier.isOK()) {
                int id = insertModeWindow.getID();
                String firstName = insertModeWindow.getFirstName();
                String lastName = insertModeWindow.getLastName();
                java.sql.Date birthdate = insertModeWindow.getBirthdate();
                String team = insertModeWindow.getTeam();
                int apps = insertModeWindow.getApps();
                int firstSquad = insertModeWindow.getFirstSquad();
                int minutes = insertModeWindow.getMinutes();
                int goals = insertModeWindow.getGoals();
                int yellowCards = insertModeWindow.getYellowCards();
                int redCards = insertModeWindow.getRedCards();
                Player player = new Player(id, firstName, lastName, birthdate);
                player.addPlayerRow(team, apps, firstSquad, minutes, goals, yellowCards, redCards, currentLeague);
                if(insertToDatabase(player)){
                    Object[] temp = {id, firstName, lastName, birthdate, team, apps, firstName, minutes, goals, yellowCards, redCards};
                    leagueView.addToPlayersTable(temp);
                    leagueView.refresh();
                }
                insertModeWindow.dispatchEvent(new WindowEvent(insertModeWindow, WindowEvent.WINDOW_CLOSING));
            }
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

    private boolean delFromDatabase(int ID, String team)
    {
        DatabaseConnection db = new DatabaseConnection();
        if(!db.createConnection())
            showDialog("Database Error", "Cannot connect to database.", 0, 0);
        else
        {
            if (db.deletePlayer(ID, team, currentLeague))
            {
                if (!db.shutdown())
                    showDialog("Database Error", "Cannot shutdown database.", 0, 0);
                return true;
            }
            if (!db.shutdown())
                showDialog("Database Error", "Cannot shutdown database.", 0, 0);
        }
        return false;
    }

    private boolean insertToDatabase(Player player)
    {
        DatabaseConnection db = new DatabaseConnection();
        if(!db.createConnection()) {
            showDialog("Database Error", "Cannot connect to database.", 0, 0);
            return false;
        }
        else
        {
            try
            {
                db.insertPlayer(player);
            }
            catch (SQLIntegrityConstraintViolationException e1)
            {
                showDialog("Database Error", "Cannot insert player " + player.getFirstName() + " " + player.getLastName() + " to database due to SQLIntegrityConstraintViolationException.", 0, 0);
                return false;
            }
            catch (SQLException e2)
            {
                showDialog("Database Error", "Cannot insert player " + player.getFirstName() + " " + player.getLastName() + " to database. Reason: " + e2.getMessage(), 0, 0);
                return false;
            }
            if (!db.shutdown())
                showDialog("Database Error", "Cannot shutdown database.", 0, 0);
            return true;
        }
    }

    private void updateDatabase(int id, String team, String leagueName, String columnName, Object newValue)
    {
        DatabaseConnection db = new DatabaseConnection();
        if(!db.createConnection())
            showDialog("Database Error", "Cannot connect to database.", 0, 0);
        else
        {
            if(!db.updatePlayersSpecificColumn(id, team, leagueName, columnName, newValue))
                showDialog("Database Error", "Cannot update this column for player ID: " + id, 0, 0);
            if (!db.shutdown())
                showDialog("Database Error", "Cannot shutdown database.", 0, 0);
        }
    }

    private void getPlayersFromLeague(LeagueView view, String leagueName, String orderBy, boolean desc)
    {
        DatabaseConnection db = new DatabaseConnection();
        if(!db.createConnection())
            showDialog("Database Error", "Cannot connect to database.", 0, 0);
        else
        {
            if(!db.updateView(view, leagueName, orderBy, desc))
                showDialog("Database Error", "Cannot get players rows from league " + leagueName, 0, 0);
            if (!db.shutdown())
                showDialog("Database Error", "Cannot shutdown database.", 0, 0);
        }
    }

    private List<String> getLeaguesNames() {
        DatabaseConnection db = new DatabaseConnection();
        if(!db.createConnection())
        {
            showDialog("Database Error", "Cannot connect to database.", 0, 0);
            System.exit(1);
        }
        else
        {
            List<String> names = db.getTablesNames();
            if(names == null)
                showDialog("Database Error", "Cannot get tables names from database", 0, 0);
            if (!db.shutdown())
                showDialog("Database Error", "Cannot shutdown database.", 0, 0);
            return names;
        }
        return null;
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

    public void updateEnded(){
        if(!isUpdateCancelled) {
            progress.log("Update finished succesfully", 1);
            progress.disableStopUpdateButton();
            progress.changeCloseOperation();
        }
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
        leagueView.addPlayersButtonListener(new AddPlayersListener());
        leagueView.addPDFButtonListener(new CreatePDFListener());
        leagueView.addEditModeButtonListener(new EditModeButtonListener());
        leagueView.addInsertModeButtonListener(new InsertModeButtonListener());
        leagueView.addDelButtonListener(new DelButtonListener());

        leaguesLinks = new LeaguesLinks(this);
    }

    HashMap<Integer, Integer> getPlayersIDs() {
        return playersIDs;
    }
}
