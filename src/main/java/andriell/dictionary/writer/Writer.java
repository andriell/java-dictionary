package andriell.dictionary.writer;

import java.util.Set;

public interface Writer {
    String getName();

    void write(String lemma, Set<String> words);

    void setBaseFileName(String baseFileName);

    void close();
}
