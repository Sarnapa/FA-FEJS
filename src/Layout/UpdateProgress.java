package Layout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by sarnapa on 06.03.17.
 */
public class UpdateProgress extends JFrame{
    private JPanel rootPanel;
    private JLabel TeamsDoneLabel;
    private JLabel TeamsDoneCount;
    private JLabel LeaguesDoneLabel;
    private JLabel LeaguesDoneCount;
    private JButton stopButton;
    private int TeamsDone = 0;
    private int LeaguesDone = 0;


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

    private void initGUI(){
        setTitle("FA-FEJS Data is being updated...");
        setSize(new Dimension(250, 120));
        setResizable(false);
        rootPanel.setBorder(new EmptyBorder(10,10,10,10));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }
}