package Layout;

import javafx.scene.control.Tab;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.HashMap;

public class LeagueView extends JFrame {

    private JComboBox<String> leagueChoice;
    private JButton updateButton;
    private JButton pdfButton;
    private JPanel rootPanel;
    private JPanel leagueChoicePanel;
    private JScrollPane playerListPane;
    private JPanel buttonPanel;
    private JTable playersTable;
    private JButton addPlayersButton;
    private JButton editModeButton;
    private DefaultTableModel tableModel;
    private MyGlassPane glassPane;
    private LayoutInit controller;
    private boolean isTableEditable = false;


    private class MyTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (controller.getPlayersIDs().containsValue(row)) {
                setForeground(Color.red);
            } else {
                setForeground(Color.black);
            }
            return (this);
        }
    }

    private void createUIComponents() {
        String[] columnNames = {"ID", "FIRST NAME", "LAST NAME", "BIRTHDATE", "TEAM", "APPS", "FIRST SQUAD", "MINUTES", "GOALS", "YELLOW CARDS", "RED CARDS"};
        tableModel = new DefaultTableModel(0, columnNames.length) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(columnIndex == 0)
                    return false; // don't change id!
                return isTableEditable;
            }
        };
        tableModel.setColumnIdentifiers(columnNames);
        playersTable = new JTable(tableModel);
        playersTable.setDefaultRenderer(Object.class, new MyTableCellRenderer());
        glassPane = new MyGlassPane();
        //changes selection method. there's no need to hold ctrl.
        /*
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
        */
    }


    public int getRowWithValue(int x) {
        int rowCount = playersTable.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            if (playersTable.getValueAt(i, 0).equals(x)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isTableEditable() {
        return isTableEditable;
    }

    public void disableView() {
        setEnabled(false);
        glassPane.setVisible(true);
    }

    public void enableView() {
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

    public HashMap<Integer, Integer> getSelectedPlayers() {
        HashMap<Integer, Integer> tmp = new HashMap<>();
        int[] players = playersTable.getSelectedRows();
        for (int player : players) {
            tmp.put((int) playersTable.getValueAt(player, 0), (player));
        }
        return tmp;
    }

    public void disableUpdateButton() {
        updateButton.setEnabled(false);
    }

    public void enableUpdateButton() {
        updateButton.setEnabled(true);
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public void addToPlayersTable(Object[] s) {
        tableModel.addRow(s);
    }

    public JTable getPlayersTable() {
        return playersTable;
    }

    public void addLeagueChoiceElement(String s) {
        leagueChoice.addItem(s);
        leagueChoice.setSelectedIndex(-1);
    }

    public boolean editMode(){
        if(isTableEditable){
            playersTable.setBorder(new EmptyBorder(0,0,0,0));
            pdfButton.setEnabled(true);
            updateButton.setEnabled(true);
            addPlayersButton.setEnabled(true);
            leagueChoice.setEnabled(true);
        } else {
            playersTable.setBorder(new LineBorder(Color.blue, 1));
            pdfButton.setEnabled(false);
            updateButton.setEnabled(false);
            addPlayersButton.setEnabled(false);
            leagueChoice.setEnabled(false);
        }
        isTableEditable = !isTableEditable;
        return isTableEditable;
    }
    public void refresh() {
        playersTable.repaint();
    }

    public LeagueView(LayoutInit _controller) {
        controller = _controller;
        pack();
        setContentPane(rootPanel);
        initGUI();
    }

    private void initGUI() {
        setTitle("FA-FEJS");
        setSize(new Dimension(1000, 600));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setGlassPane(glassPane);
        leagueChoice.setEditable(false);
        setVisible(true);
    }


    public void addLeagueChoiceListener(ActionListener listenerForLeagueChoiceButton) {
        leagueChoice.addActionListener(listenerForLeagueChoiceButton);
    }

    public void addTableHeaderListener(MouseAdapter listenerForTableHeader) {
        playersTable.getTableHeader().addMouseListener(listenerForTableHeader);
    }

    public void addUpdateButtonListener(ActionListener listenerForUpdateButton) {
        updateButton.addActionListener(listenerForUpdateButton);
    }

    public void addPDFButtonListener(ActionListener listenerForPDFButton) {
        pdfButton.addActionListener(listenerForPDFButton);
    }

    public void addPlayersButtonListener(ActionListener listenerForPlayersButton) {
        addPlayersButton.addActionListener(listenerForPlayersButton);
    }
    public void addEditModeButtonListener(ActionListener listenerForEditModeButton) {
        editModeButton.addActionListener(listenerForEditModeButton);
    }
    public void addTableModelListener(TableModelListener listenerForTableModel) {
        tableModel.addTableModelListener(listenerForTableModel);
    }
    public void removeTableModelListener() {
        TableModelListener[] list = tableModel.getTableModelListeners();
        tableModel.removeTableModelListener(list[0]);
    }
}
