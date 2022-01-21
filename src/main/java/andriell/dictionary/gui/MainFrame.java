package andriell.dictionary.gui;

import javax.swing.*;

/**
 * Created by Rybalko on 30.08.2016.
 */
public class MainFrame {
    private JTabbedPane rootTabbedPane;
    private JPanel rootPane;

    private JFrame frame;

    public void init() {
        frame = new JFrame("Dictionary");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/dictionary.png"));
        frame.setIconImage(icon.getImage());

        frame.setContentPane(rootPane);

        frame.setVisible(true);
    }
}
