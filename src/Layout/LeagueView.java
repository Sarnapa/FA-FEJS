package Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

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
    private JButton addPlayersButton;
    private DefaultTableModel tableModel;
    private MyGlassPane glassPane;

    private ArrayList<Integer> selectedToPDF = new ArrayList<>();

    class CustomRenderer extends DefaultTableCellRenderer //implements TableCellRenderer
    {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color foreground, background;
            foreground = null;
            background = null;



            if(isSelected){
                foreground = Color.blue;
                background = Color.white;
            }else{
                if (selectedToPDF.contains(row)) {
                    foreground = Color.red;
                    background = Color.white;
                } else {
                    foreground = Color.white;
                    background = Color.blue;
                }
            }

            /*else {
                Random random = new Random();

             */

                /*
                switch(row%6) {
                    case 1:
                        foreground = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        background = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        break;
                    case 2:
                        foreground = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        background = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        break;
                    case 3:
                        foreground = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        background = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        break;
                    case 4:
                        foreground = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        background = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        break;
                    case 5:
                        foreground = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        background = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        break;
                    case 6:
                        foreground = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        background = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
                        break;
                    default:
                        break;
                }*/
            //}
            renderer.setForeground(foreground);
            renderer.setBackground(background);
            return renderer;
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
        //playersTable.setDefaultRenderer(Object.class, new CustomRenderer());
        glassPane = new MyGlassPane();
        //changes selection method. there's no need to hold ctrl.
        playersTable.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int x, int y) {
                if(super.isSelectedIndex(x)) {
                    super.removeSelectionInterval(x, y);
                }
                else {
                    super.addSelectionInterval(x, y);
                }
            }
        });
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

    public void addPDFButtonListener(ActionListener listenerForPDFButton){
       pdfButton.addActionListener(listenerForPDFButton);
    }
    public void addPlayersButtonListener(ActionListener listenerForPlayersButton){
        addPlayersButton.addActionListener(listenerForPlayersButton);
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

    public java.util.List<String> getRowAt(int row) {
        int colNumber = playersTable.getColumnCount();
        java.util.List<String> result = new java.util.ArrayList<>();

        for (int i = 0; i < colNumber; i++) {
            result.add(tableModel.getValueAt(row, i).toString());
        }

        return result;
    }

    public int[] getSelectedPlayers(){
        int[] players = playersTable.getSelectedRows();
        int[] result = new int[players.length];
        for(int i = 0;i < players.length;i++){
            result[i] = (int)playersTable.getValueAt(players[i], 0);
        }
        return result;
    }

    public void clearTable(){
        tableModel.setRowCount(0);
    }
    public void addToPlayersTable(Object[] s){
        tableModel.addRow(s);
    }
    public JTable getPlayersTable(){ return playersTable; }

    public void addLeagueChoiceElement(String s){
        leagueChoice.addItem(s);
        leagueChoice.setSelectedIndex(-1);
    }

    public void refresh(){
        playersTable.repaint();
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
