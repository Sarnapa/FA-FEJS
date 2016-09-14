package Layout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by Pawel on 06-Sep-16.
 */
public class LeagueView extends JFrame {

    private JComboBox leagueChoice;
    private DefaultListModel model;
    private JList playersList;
    private JButton updateButton;
    private JButton pdfButton;
    private JPanel rootPanel;
    private JPanel leagueChoicePanel;
    private JScrollPane playerListPane;
    private JPanel buttonPanel;

    //private String[] leagues = {"CENTRALNA LIGA JUNIORÓW GR. WSCHODNIA", "CENTRALNA LIGA JUNIORÓW GR. ZACHODNIA", "DRUGA LIGA", "EKSTRAKLASA", "TRZECIA LIGA GRUPA I", "TRZECIA LIGA GRUPA II", "TRZECIA LIGA GRUPA III", "TRZECIA LIGA GRUPA IV"};

    private void createUIComponents() {
        model = new DefaultListModel();
        playersList = new JList(model);
    }

    public void addLeagueChoiceListener(ActionListener listenerForLeagueChoiceButton){
        leagueChoice.addActionListener(listenerForLeagueChoiceButton);
    }

    public String getLeagueChoiceSelected() {
        return leagueChoice.getSelectedItem().toString();
    }

    public void clearList(){
        model.clear();
    }
    public void addToPlayersList(String s){
        model.addElement(s);
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
    private void fillList(){

        String s = new String("s");
        for(int i = 0; i<10; i++){
            s = s + Integer.toString(i);
            model.addElement(s);
        }
    }

    public LeagueView(){
        pack();
        setContentPane(rootPanel);
        fillLeagueChoice();
        //fillList();
        initGUI();
    }

    private void initGUI(){
        setTitle("FA-FEJS");
        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
