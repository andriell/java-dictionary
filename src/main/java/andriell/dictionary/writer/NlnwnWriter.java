package andriell.dictionary.writer;

import andriell.dictionary.service.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class NlnwnWriter implements Writer {
    private static BufferedWriter writer;

    @Override public String getName() {
        return "\\n Lemma \\n word \\n .txt";
    }

    @Override public void write(String lemma, Set<String> words) {
        try {
            if (lemma == null)
                return;

            writer.write("\n");
            writer.write(lemma);
            writer.write("\n");

            if (words == null) {
                return;
            }
            for (String word : words) {
                writer.write(word);
                writer.write("\n");
            }
        } catch (Exception e) {
            Log.error(e);
        }

    }

    @Override public void setBaseFileName(String baseFileName) {
        try {
            writer = new BufferedWriter(new FileWriter(new File(baseFileName + "_nlnwn.txt")));
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void close() {
        try {
            if (writer != null)
                writer.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
