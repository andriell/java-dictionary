package andriell.dictionary.writer;

import andriell.dictionary.helpers.FileHelper;
import andriell.dictionary.service.Log;

import java.io.File;
import java.io.Writer;
import java.util.Set;

public class LtwnWriter implements DicWriter, HaveLemmeLemma {
    private Writer writer;
    private boolean isLemmaLemma = false;

    @Override
    public String getName() {
        return "Lemma \\t word \\n .csv";
    }

    @Override
    public void setBaseFileName(String baseFileName) {
        try {
            writer = FileHelper.makeWriter(new File(baseFileName + "_ltwn.csv"));
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void begin() {

    }

    @Override public void write(String lemma, Set<String> words) {
        try {
            if (lemma == null)
                return;
            if (isLemmaLemma || words == null) {
                writer.write(lemma);
                writer.write("\t");
                writer.write(lemma);
                writer.write("\n");
            }
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

    @Override
    public void close() {
        try {
            if (writer != null)
                writer.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override
    public void setLemmeLemma(boolean b) {
        isLemmaLemma = b;
    }
}
