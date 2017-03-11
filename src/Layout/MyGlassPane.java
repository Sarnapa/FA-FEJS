package Layout;

import javax.swing.*;
import java.awt.*;

class MyGlassPane extends JComponent {
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(250,250,250,150));
        g.fillRect(0,0,getWidth()-1, getHeight()-1);
    }
}