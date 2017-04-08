package Layout;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by blank on 4/7/2017.
 */
public class InsertModeWindow extends JFrame {
    private JPanel rootPanel;
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField teamField;
    private JTextField appsField;
    private JTextField firstSquadField;
    private JTextField minutesField;
    private JTextField goalsField;
    private JTextField yellowCardsField;
    private JButton insertButton;
    private JTextField redCardsField;
    private JDatePickerImpl birthdateField;


    public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }

            return "";
        }

    }

    private void createUIComponents() {
        SqlDateModel model = new SqlDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        birthdateField = new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    InsertModeWindow() {
        initGUI();
    }

    private void initGUI() {
        setContentPane(rootPanel);
        rootPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setTitle("FA-FEJS Data updater");
        setSize(new Dimension(360, 360));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    void addInsertButtonListener(ActionListener listenerForInsertButton) {
        insertButton.addActionListener(listenerForInsertButton);
    }

    void addInsertModeWindowListener(WindowAdapter listenerForInsertModeWindow) {
        addWindowListener(listenerForInsertModeWindow);
    }

    public String getID(){return idField.getText();}
    public String getFirstName(){return firstNameField.getText();}
    public String getLastName(){return lastNameField.getText();}
    public java.sql.Date getBirthdate(){return (java.sql.Date)birthdateField.getModel().getValue();}
    public String getTeam(){return teamField.getText();}
    public String getApps(){return appsField.getText();}
    public String getFirstSquad(){return firstSquadField.getText();}
    public String getMinutes(){return minutesField.getText();}
    public String getGoals(){return goalsField.getText();}
    public String getYellowCards(){return yellowCardsField.getText();}
    public String getRedCards(){return redCardsField.getText();}
}
