package andriell.dictionary.gui;

import andriell.dictionary.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FontIcon implements Icon {
    private int width = 16;
    private int height = 16;
    private Image image;

    private FontIcon(char code, int iconSize, Color iconColor) {
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/font/awesome.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        BufferedImage buffer = new BufferedImage(width, height, 2);
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font.deriveFont(0, (float) iconSize));
        g2.setColor(iconColor);
        int sy = g2.getFontMetrics().getAscent();
        g2.drawString(String.valueOf(code), 0, sy);
        g2.dispose();
        image = buffer;
    }

    public static FontIcon of(char code) {
        return new FontIcon(code, 16, Color.BLACK);
    }


    public static FontIcon of(char code, Color iconColor) {
        return new FontIcon(code, 16, iconColor);
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    public int getIconHeight() {
        return this.height;
    }

    public int getIconWidth() {
        return this.width;
    }
}
