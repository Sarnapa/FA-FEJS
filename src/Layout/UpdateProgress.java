package Layout;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateProgress extends JFrame{
    private JPanel rootPanel;
    private JLabel TeamsDoneLabel;
    private JLabel TeamsDoneCount;
    private JLabel LeaguesDoneLabel;
    private JLabel LeaguesDoneCount;
    private JButton stopButton;
    private JTextPane logArea;
    private JScrollPane logScrollPane;
    private JPanel labelsPanel;
    private JLabel label;
    private int TeamsDone = 0;
    private int LeaguesDone = 0;
    private Style style;

    private void createUIComponents() {
        logArea = new JTextPane();
        style = logArea.addStyle("MyStyle", null);
        logArea.setBorder(new LineBorder(Color.gray, 1));
        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(new EmptyBorder(3,3,3,3));
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void addProgressListener(ActionListener listenerForProgressStopButton){
        stopButton.addActionListener(listenerForProgressStopButton);
    }

    public UpdateProgress() {
        pack();
        setContentPane(rootPanel);
        initGUI();
    }

    public void updateTeamsCount(){
        ++TeamsDone;
        TeamsDoneCount.setText(Integer.toString(TeamsDone));
    }

    public void updateLeaguesCount(){
        ++LeaguesDone;
        LeaguesDoneCount.setText(Integer.toString(LeaguesDone));
    }

    public void log(String s, int level){

        switch(level){
            case 0:         // log
                StyleConstants.setForeground(style, Color.black);
                break;
            case 1:         // info
                StyleConstants.setForeground(style, Color.blue);
                break;
            case 2:         // error
                StyleConstants.setForeground(style, Color.red);
                break;
            default:
                break;
        }


        String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        try {
            Document doc = logArea.getDocument();
            doc.insertString(doc.getLength(), timestamp + ": " + s + "\n", style);
        } catch(BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    private void setBorders(){
        TitledBorder border;
        border = BorderFactory.createTitledBorder("FA-FEJS data is being update...");
        labelsPanel.setBorder(new CompoundBorder(
                border,
                new EmptyBorder(5,5,5,5)));
    }

    public void changeCloseOperation(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initGUI(){
        setTitle("FA-FEJS");
        setSize(new Dimension(700, 500));
        setResizable(true);
        rootPanel.setBorder(new EmptyBorder(5,5,5,5));
        setBorders();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }
}