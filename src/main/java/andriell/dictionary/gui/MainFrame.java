package andriell.dictionary.gui;

import andriell.dictionary.service.Log;
import andriell.dictionary.service.Parser;
import andriell.dictionary.writer.Writers;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

/**
 * Created by Rybalko on 30.08.2016.
 */
public class MainFrame {
    private JPanel rootPane;
    private JTextPane textPane;
    private JProgressBar progressBar;
    private JButton openButton;
    private JComboBox comboBox;
    private JButton saveButton;

    private JFrame frame;

    private JFileChooser dataFileChooser;
    private File fileDic;
    Parser parser;

    public void init() {
        frame = new JFrame("Dictionary");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                Log.closeFileLog();
            }
        });

        ImageIcon icon = new ImageIcon(getClass().getResource("/img/dictionary.png"));
        frame.setIconImage(icon.getImage());

        frame.setContentPane(rootPane);

        parser = new Parser();
        //<editor-fold desc="progressBar">
        parser.setProgressListener(new Parser.ProgressListener() {
            @Override public void onStart(int max) {
                progressBar.setMaximum(max);
                openButton.setEnabled(false);
                comboBox.setEnabled(false);
                saveButton.setEnabled(false);
            }

            @Override public void onUpdate(int max, int position) {
                progressBar.setValue(position);
            }

            @Override public void onComplete() {
                progressBar.setMaximum(100);
                progressBar.setValue(0);
                openButton.setEnabled(true);
                comboBox.setEnabled(true);
                saveButton.setEnabled(true);
            }
        });
        //</editor-fold>

        //<editor-fold desc="textArea">
        textPane.setBackground(frame.getBackground());
        //</editor-fold>

        //<editor-fold desc="dataFileChooser">
        String defaultPath = new File(".").getAbsolutePath();
        dataFileChooser = new JFileChooser(
                Preferences.get(Preferences.LAST_USED_FOLDER_DATA_PASS, defaultPath));
        FileNameExtensionFilter ff = buildFilter("dic", new String[] { "dic" });
        dataFileChooser.addChoosableFileFilter(ff);
        dataFileChooser.setFileFilter(ff);

        Dimension dimension = (Dimension) Preferences
                .getSerializable(Preferences.LAST_USED_DIMENSION,
                        dataFileChooser.getPreferredSize());
        dataFileChooser.setPreferredSize(dimension);
        if (fileDic != null && fileDic.isFile()) {
            dataFileChooser.setSelectedFile(fileDic);
        }
        //</editor-fold>

        //<editor-fold desc="openButton">
        openButton.setIcon(FontIcon.of(FontAwesome.FOLDER_OPEN, Color.GRAY));
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int ret = dataFileChooser.showOpenDialog(rootPane);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    fileDic = dataFileChooser.getSelectedFile();

                    Preferences.put(Preferences.LAST_USED_FOLDER_DATA_PASS,
                            dataFileChooser.getSelectedFile().getParent());
                    update();
                }
                Preferences.putSerializable(Preferences.LAST_USED_DIMENSION,
                        dataFileChooser.getSize());
                dataFileChooser.setPreferredSize(dataFileChooser.getSize());
            }
        });
        //</editor-fold>

        //<editor-fold desc="comboBox">
        DefaultComboBoxModel model = new DefaultComboBoxModel(Writers.getNames());
        comboBox.setModel(model);
        //</editor-fold>

        //<editor-fold desc="saveButton">
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int i = comboBox.getSelectedIndex();
                    parser.setDicWriter(Writers.getWriter(i));
                    parser.setFileDic(fileDic);
                    parser.parse();
                } catch (Exception e1) {
                    Log.error(e1);
                }
                update();
            }
        });
        //</editor-fold>

        //<editor-fold desc="Preferences">
        Rectangle bounds = (Rectangle) Preferences
                .getSerializable(Preferences.LAST_USED_BOUNDS, new Rectangle(0, 0, 550, 180));
        frame.setBounds(bounds);
        frame.addComponentListener(new ComponentListener() {
            @Override public void componentResized(ComponentEvent e) {
                Preferences.putSerializable(Preferences.LAST_USED_BOUNDS, frame.getBounds());
            }

            @Override public void componentMoved(ComponentEvent e) {
                Preferences.putSerializable(Preferences.LAST_USED_BOUNDS, frame.getBounds());
            }

            @Override public void componentShown(ComponentEvent e) {

            }

            @Override public void componentHidden(ComponentEvent e) {

            }
        });
        //</editor-fold>

        frame.setVisible(true);
        update();
    }

    private void update() {
        saveButton.setEnabled(fileDic != null);
        if (saveButton.isEnabled()) {
            saveButton.setIcon(FontIcon.of(FontAwesome.SAVE, Color.GRAY));
        } else {
            saveButton.setIcon(FontIcon.of(FontAwesome.SAVE, Color.LIGHT_GRAY));
        }
    }

    public static FileNameExtensionFilter buildFilter(String description, String[] extensions) {
        StringBuilder builder = new StringBuilder();
        builder.append(description);
        builder.append(" (");
        String prefix = "";
        for (String extension : extensions) {
            builder.append(prefix);
            builder.append("*.");
            builder.append(extension);
            prefix = ", ";
        }
        builder.append(")");
        return new FileNameExtensionFilter(builder.toString(), extensions);
    }
}
