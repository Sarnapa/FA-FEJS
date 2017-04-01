package Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

public class UpdateView extends JFrame {
    private JPanel rootPanel;
    private JButton updateButton;
    private JList<String> leaguesList;
    private JScrollPane leaguesListPane;
    private DefaultListModel<String> listModel;
    private MyGlassPane glassPane;

    private void createUIComponents() {
        glassPane = new MyGlassPane();
        listModel = new DefaultListModel<>();
        leaguesList = new JList<>(listModel);
        leaguesListPane = new JScrollPane(leaguesList);
        leaguesList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int x, int y) {
                if (super.isSelectedIndex(x)) {
                    super.removeSelectionInterval(x, y);
                } else {
                    super.addSelectionInterval(x, y);
                }
            }
        });


    }

    public void disableView() {
        setEnabled(false);
        glassPane.setVisible(true);
    }

    public void enableView() {
        setEnabled(true);
        glassPane.setVisible(false);
    }

    public void addToLeaguesList(String s) {
        listModel.addElement(s);
    }

    public UpdateView() {
        pack();
        setContentPane(rootPanel);
        updateButton.setText("Update");
        leaguesList.setBorder(new EmptyBorder(5, 5, 5, 5));
        rootPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        initGUI();
    }

    public void addUpdateWindowListener(WindowAdapter listenerForUpdateWindow) {
        addWindowListener(listenerForUpdateWindow);
    }

    public void addUpdateListener(ActionListener listenerForUpdate) {
        updateButton.addActionListener(listenerForUpdate);
    }

    public java.util.List<String> getSelectedLeagues() {
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
