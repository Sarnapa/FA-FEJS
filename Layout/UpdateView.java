package Layout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

/**
 * Created by Pawel on 21-Oct-16.
 */
public class UpdateView extends JFrame{
    private JCheckBox checkBox1;
    private JPanel rootPanel;
    private JTextArea textArea1;


    public UpdateView() {
        pack();
        setContentPane(rootPanel);
        initGUI();
    }

    public void addUpdateWindowListener(WindowAdapter listenerforUpdateWindow){
        addWindowListener(listenerforUpdateWindow);
    }
    private void initGUI() {
        setTitle("FA-FEJS Data update");
        setSize(new Dimension(300, 600));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
