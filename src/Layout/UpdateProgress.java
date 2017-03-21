package Layout;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
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
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    private JPanel labelsPanel;
    private JLabel label;
    private int TeamsDone = 0;
    private int LeaguesDone = 0;

    private void createUIComponents() {
        logArea = new JTextArea();
        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(new EmptyBorder(5,5,5,5));
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

    public void log(String s){
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        logArea.append(timestamp + ": " + s + "\n");
    }

    private void setBorders(){
        TitledBorder border;
        border = BorderFactory.createTitledBorder("FA-FEJS data is being update...");
        labelsPanel.setBorder(new CompoundBorder(
                border,
                new EmptyBorder(5,5,5,5)));
    }

    private void initGUI(){
        setTitle("FA-FEJS");
        setSize(new Dimension(250, 300));
        setResizable(false);
        rootPanel.setBorder(new EmptyBorder(5,5,5,5));
        setBorders();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }
}