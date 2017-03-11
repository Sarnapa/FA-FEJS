package Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

/**
 * Created by Pawel on 21-Oct-16.
 */
public class UpdateView extends JFrame{
    private JPanel rootPanel;
    private JButton updateButton;
    private JList leaguesList;
    private JScrollPane leaguesListPane;
    private DefaultListModel listModel;
    private MyGlassPane glassPane;

    private void createUIComponents() {
        glassPane = new MyGlassPane();
        listModel = new DefaultListModel();
        leaguesList = new JList(listModel);
        leaguesListPane = new JScrollPane(leaguesList);
    }

    public void disableView(){
        setEnabled(false);
        glassPane.setVisible(true);
    }

    public void enableView(){
        setEnabled(true);
        glassPane.setVisible(false);
    }

    public void addToLeaguesList(String s){
        listModel.addElement(s);
    }

    public UpdateView() {
        pack();
        setContentPane(rootPanel);
        updateButton.setText("Update");
        leaguesList.setBorder(new EmptyBorder(5,5,5,5));
        rootPanel.setBorder(new EmptyBorder(5,5,5,5));
        initGUI();
    }

    public void addUpdateWindowListener(WindowAdapter listenerForUpdateWindow){
        addWindowListener(listenerForUpdateWindow);
    }

    public void addUpdateListener(ActionListener listenerForUpdate){
        updateButton.addActionListener(listenerForUpdate);
    }

    public java.util.List<String> getSelectedLeagues(){
        return leaguesList.getSelectedValuesList();
    }

    private void initGUI() {
        setTitle("FA-FEJS Data updater");
        setSize(new Dimension(350, 600));
        setGlassPane(glassPane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
