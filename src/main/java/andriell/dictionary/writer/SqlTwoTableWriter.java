package andriell.dictionary.writer;

import andriell.dictionary.helpers.StringHelper;
import andriell.dictionary.service.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class SqlTwoTableWriter implements Writer {
    private BufferedWriter writer;
    private BufferedWriter writerCreate;
    private int lemmaId = 0;

    @Override public String getName() {
        return "Sql two table .sql";
    }

    @Override public void write(String lemma, Set<String> words) {
        try {
            if (writerCreate != null) {
                writerCreate.write("CREATE TABLE `dic_lemma` (\n"
                        + "\t`id` INT(11) NOT NULL,\n"
                        + "\t`word` VARCHAR(255) NOT NULL,\n"
                        + "\tPRIMARY KEY (`id`)\n"
                        + ");\n");
                writerCreate.write("CREATE TABLE `dic_word` (\n"
                        + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
                        + "\t`dic_lemma_id` INT(11) NOT NULL,\n"
                        + "\t`word` VARCHAR(255) NOT NULL,\n"
                        + "\tPRIMARY KEY (`id`)\n"
                        + ")\n");
                writerCreate.flush();
                writerCreate.close();
                writerCreate = null;
            }
        } catch (Exception e) {
            Log.error(e);
        }
        // INSERT INTO lemma(id, word) VALUES ('lemma','lemma'), ('lemma','word1'), ('lemma','word2');
        try {
            if (lemma == null)
                return;
            lemmaId++;

            writer.write("INSERT INTO dic_lemma (id, word) VALUES (");
            writer.write(Integer.toString(lemmaId));
            writer.write(", ");
            writer.write(StringHelper.sqlEscape(lemma));
            writer.write(");\n");

            if (words == null) {
                return;
            }
            writer.write("INSERT INTO dic_word (dic_lemma_id, word) VALUES ");
            String sfx = "";
            for (String word : words) {
                writer.write(sfx);
                writer.write("(");
                writer.write(Integer.toString(lemmaId));
                writer.write(", ");
                writer.write(StringHelper.sqlEscape(word));
                writer.write(")");
                sfx = ", ";
            }
            writer.write(";\n");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void setBaseFileName(String baseFileName) {
        try {
            writer = new BufferedWriter(new FileWriter(new File(baseFileName + "_two_table.sql")));
            writerCreate = new BufferedWriter(new FileWriter(new File(baseFileName + "_two_table_create.sql")));
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
