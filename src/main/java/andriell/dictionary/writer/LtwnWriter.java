package andriell.dictionary.writer;

import andriell.dictionary.service.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class LtwnWriter implements Writer {
    private BufferedWriter writer;

    @Override public String getName() {
        return "Lemma \\t word \\n .txt";
    }

    @Override public void write(String lemma, Set<String> words) {
        try {
            if (lemma == null)
                return;
            writer.write(lemma);
            writer.write("\t");
            writer.write(lemma);
            writer.write("\n");
            if (words == null) {
                return;
            }
            for (String word : words) {
                writer.write(lemma);
                writer.write("\t");
                writer.write(word);
                writer.write("\n");
            }
        } catch (Exception e) {
            Log.error(e);
        }

    }

    @Override public void setBaseFileName(String baseFileName) {
        try {
            writer = new BufferedWriter(new FileWriter(new File(baseFileName + "_ltwn.txt")));
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
