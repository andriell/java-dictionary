package andriell.dictionary.writer;

import java.util.Set;

public interface DicWriter {
    String getName();

    void setBaseFileName(String baseFileName);

    void begin();

    void write(String lemma, Set<String> words);

    void close();
}
