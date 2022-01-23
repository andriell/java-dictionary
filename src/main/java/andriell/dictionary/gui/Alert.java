package andriell.dictionary.gui;

import javax.swing.*;

public class Alert {
    public static void savedSuccessfully(long wordsCount) {
        JOptionPane.showMessageDialog(null, "Данные успешно сохранены. Всего слов: " + wordsCount);
    }

    public static void savedSuccessfully(long wordsCount, long lastIndex) {
        JOptionPane.showMessageDialog(null, "Данные успешно сохранены. Всего слов: " + wordsCount + ". Последний добавленный индекс: " + lastIndex);
    }
}
