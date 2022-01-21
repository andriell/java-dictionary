package andriell.dictionary.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

/**
 * Created by Rybalko on 30.08.2016.
 */
public class MainFrame {
    private JTabbedPane rootTabbedPane;
    private JPanel rootPane;

    private JFrame frame;

    public void init() {
        frame = new JFrame("Crypto XOR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/keys-access.png"));
        frame.setIconImage(icon.getImage());

        frame.setContentPane(rootPane);

        frame.setVisible(true);
    }
}
