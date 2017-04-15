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

public class InsertModeWindow extends JFrame {
    private JPanel rootPanel;
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JDatePickerImpl birthdateField;
    private JTextField teamField;
    private JTextField appsField;
    private JTextField firstSquadField;
    private JTextField minutesField;
    private JTextField goalsField;
    private JTextField yellowCardsField;
    private JTextField redCardsField;
    private JButton insertButton;
    private InsertModeInputVerifier verifier;


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

    InsertModeWindow(InsertModeInputVerifier verifier)
    {
        this.verifier = verifier;
        setInputVerifier();
        initGUI();
    }

    private void setInputVerifier()
    {
        idField.setName("idField");
        idField.setInputVerifier(verifier);
        firstNameField.setName("firstNameField");
        firstNameField.setInputVerifier(verifier);
        lastNameField.setName("lastNameField");
        lastNameField.setInputVerifier(verifier);
        teamField.setName("teamField");
        teamField.setInputVerifier(verifier);
        appsField.setName("appsField");
        appsField.setInputVerifier(verifier);
        firstSquadField.setName("firstSquadField");
        firstSquadField.setInputVerifier(verifier);
        minutesField.setName("minutesField");
        minutesField.setInputVerifier(verifier);
        goalsField.setName("goalsField");
        goalsField.setInputVerifier(verifier);
        yellowCardsField.setName("yellowCardsField");
        yellowCardsField.setInputVerifier(verifier);
        redCardsField.setName("redCardsField");
        redCardsField.setInputVerifier(verifier);
    }

    private void initGUI() {
        setContentPane(rootPanel);
        rootPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setTitle("FA-FEJS Insert Mode");
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

    int getID(){return Integer.parseInt(idField.getText());}
    String getFirstName(){return firstNameField.getText();}
    String getLastName(){return lastNameField.getText();}
    java.sql.Date getBirthdate(){return (java.sql.Date)birthdateField.getModel().getValue();}
    String getTeam(){return teamField.getText();}
    int getApps(){return Integer.parseInt(appsField.getText());}
    int getFirstSquad(){return Integer.parseInt(firstSquadField.getText());}
    int getMinutes(){return Integer.parseInt(minutesField.getText());}
    int getGoals(){return Integer.parseInt(goalsField.getText());}
    int getYellowCards(){return Integer.parseInt(yellowCardsField.getText());}
    int getRedCards(){return Integer.parseInt(redCardsField.getText());}
}
