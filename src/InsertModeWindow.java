import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by blank on 4/7/2017.
 */
public class InsertModeWindow extends JFrame{
    private JPanel rootPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
    private JButton button1;
    private JButton button2;
    private JTextField textField11;

    InsertModeWindow(){
        initGUI();
    }
    private void initGUI() {
        setContentPane(rootPanel);
        rootPanel.setBorder(new EmptyBorder(5,5,5,5));
        setTitle("FA-FEJS Data updater");
        setSize(new Dimension(350, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
