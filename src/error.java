import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.PrintStream;

public class error extends JFrame {
    private JPanel panel1;
    private JTextArea textArea;
    private JScrollPane scrollPane1;


    private void createUIComponents() {
        textArea = new JTextArea();
        scrollPane1 = new JScrollPane(textArea);
        textArea.setBorder(new EmptyBorder(3, 3, 3, 3));
        scrollPane1.setBorder(new EmptyBorder(3, 3, 3, 3));
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setEditable(false);
    }

    public error() {
        pack();
        setContentPane(panel1);

        PrintStream print_stream = new PrintStream(new CustomOutputStream(textArea));
        System.setOut(print_stream);
        System.setErr(print_stream);

        initGUI();
    }


    private void initGUI() {
        setTitle("ERROR LOG");
        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel1.setBorder(new EmptyBorder(2, 2, 2, 2));
        setVisible(true);
    }
}