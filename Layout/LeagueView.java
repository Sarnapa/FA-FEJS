package Layout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by Pawel on 06-Sep-16.
 */
public class LeagueView extends JFrame {

    private JComboBox leagueChoice;
    private JButton updateButton;
    private JButton pdfButton;
    private JPanel rootPanel;
    private JPanel leagueChoicePanel;
    private JScrollPane playerListPane;
    private JPanel buttonPanel;
    private JTable playersTable;
    private DefaultTableModel tableModel;

    //private String[] leagues = {"CENTRALNA LIGA JUNIORÓW GR. WSCHODNIA", "CENTRALNA LIGA JUNIORÓW GR. ZACHODNIA", "DRUGA LIGA", "EKSTRAKLASA", "TRZECIA LIGA GRUPA I", "TRZECIA LIGA GRUPA II", "TRZECIA LIGA GRUPA III", "TRZECIA LIGA GRUPA IV"};
    //private String[] columnNames ={"ID", "FIRST_NAME", "LAST_NAME", "BIRTHDATE", "TEAM", "APPS", "FIRST_SQUAD", "MINUTES", "GOALS", "YELLOW_CARDS", "RED_CARDS"};

    private void createUIComponents() {
        String[] columnNames ={"ID", "FIRST_NAME", "LAST_NAME", "BIRTHDATE", "TEAM", "APPS", "FIRST_SQUAD", "MINUTES", "GOALS", "YELLOW_CARDS", "RED_CARDS"};
        tableModel = new DefaultTableModel(0, columnNames.length);
        tableModel.setColumnIdentifiers(columnNames);
        playersTable = new JTable(tableModel);
    }

    public void addLeagueChoiceListener(ActionListener listenerForLeagueChoiceButton){
        leagueChoice.addActionListener(listenerForLeagueChoiceButton);
    }

    public String getLeagueChoiceSelected() {
        return leagueChoice.getSelectedItem().toString();
    }

    public void clearTable(){
        tableModel.setRowCount(0);
    }
    public void addToPlayersTable(String[] s){
        tableModel.addRow(s);
    }

    public void fillLeagueChoice(){
        leagueChoice.addItem("CENTRALNA LIGA JUNIOROW GR. WSCHODNIA");
        leagueChoice.addItem("CENTRALNA LIGA JUNIOROW GR. ZACHODNIA");
        leagueChoice.addItem("DRUGA LIGA");
        leagueChoice.addItem("EKSTRAKLASA");
        leagueChoice.addItem("TRZECIA LIGA GRUPA I");
        leagueChoice.addItem("TRZECIA LIGA GRUPA II");
        leagueChoice.addItem("TRZECIA LIGA GRUPA III");
        leagueChoice.addItem("TRZECIA LIGA GRUPA IV");

        leagueChoice.setSelectedIndex(-1);
    }

    public LeagueView(){
        pack();
        setContentPane(rootPanel);
        fillLeagueChoice();
        initGUI();
    }

    private void initGUI(){
        setTitle("FA-FEJS");
        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
