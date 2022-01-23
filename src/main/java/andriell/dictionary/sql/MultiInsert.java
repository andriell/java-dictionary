package andriell.dictionary.sql;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MultiInsert implements Closeable, Flushable {
    private String beginString; // INSERT INTO dic_words (lemma, word) VALUES
    List<MultiInsertEntity> list = new ArrayList<>();
    private int maxSize = 1000;
    private Writer writer;

    public void addEntity(MultiInsertEntity entity) throws IOException {
        list.add(entity);
        if (list.size() < maxSize)
            return;
        write();
    }

    private void write() throws IOException {
        writer.append(beginString);
        String pfx = "";
        for (MultiInsertEntity en: list) {
            writer.append(pfx);
            writer.append(en.toSql());
            pfx = ", ";
        }
        writer.append(";\n");
        list.clear();
    }

    @Override public void close() throws IOException {
        write();
        writer.close();
    }

    @Override public void flush() throws IOException {
        writer.flush();
    }

    //<editor-fold desc="Getters and Setters">
    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }
    //</editor-fold>
}
