package Layout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

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
    private MyGlassPane glassPane;

    class MyGlassPane extends JComponent{
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(250,250,250,150));
            g.fillRect(0,0,getWidth()-1, getHeight()-1);
        }
    }


    private void createUIComponents() {
        String[] columnNames ={"ID", "FIRST NAME", "LAST NAME", "BIRTHDATE", "TEAM", "APPS", "FIRST SQUAD", "MINUTES", "GOALS", "YELLOW CARDS", "RED CARDS"};
        tableModel = new DefaultTableModel(0, columnNames.length){
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);
        playersTable = new JTable(tableModel);
        glassPane = new MyGlassPane();
    }

    public void addLeagueChoiceListener(ActionListener listenerForLeagueChoiceButton){
        leagueChoice.addActionListener(listenerForLeagueChoiceButton);
    }

    public void addTableHeaderListener(MouseAdapter listenerForTableHeader){
        playersTable.getTableHeader().addMouseListener(listenerForTableHeader);
    }

    public void addUpdateButtonListener(ActionListener listenerForUpdateButton){
        updateButton.addActionListener(listenerForUpdateButton);
    }

    public void disableView(){
        setEnabled(false);
        glassPane.setVisible(true);
    }

    public void enableView(){
        setEnabled(true);
        glassPane.setVisible(false);
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
    public JTable getPlayersTable(){ return playersTable; }

    public void addLeagueChoiceElement(String s){
        leagueChoice.addItem(s);
        leagueChoice.setSelectedIndex(-1);
    }

    public LeagueView()
    {
        pack();
        setContentPane(rootPanel);
        initGUI();
    }

    private void initGUI()
    {
        setTitle("FA-FEJS");
        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setGlassPane(glassPane);
        setVisible(true);
    }
}
