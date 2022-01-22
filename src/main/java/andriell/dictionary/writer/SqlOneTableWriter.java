package andriell.dictionary.writer;

import andriell.dictionary.helpers.StringHelper;
import andriell.dictionary.service.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class SqlOneTableWriter implements Writer {
    private BufferedWriter writer;
    private BufferedWriter writerCreate;

    @Override public String getName() {
        return "Sql one table .sql";
    }

    @Override public void write(String lemma, Set<String> words) {
        try {
            if (writerCreate != null) {
                writerCreate.write("CREATE TABLE `dic_words` (\n"
                        + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
                        + "\t`lemma` VARCHAR(255) NULL DEFAULT NULL,\n"
                        + "\t`word` VARCHAR(255) NULL DEFAULT NULL,\n"
                        + "\tPRIMARY KEY (`id`)\n"
                        + ");\n");
                writerCreate.flush();
                writerCreate.close();
                writerCreate = null;
            }
        } catch (Exception e) {
            Log.error(e);
        }
        // INSERT INTO words('lemma', 'word') VALUES ('lemma','lemma'), ('lemma','word1'), ('lemma','word2');
        try {
            if (lemma == null)
                return;
            writer.write("INSERT INTO dic_words (lemma, word) VALUES (");
            writer.write(StringHelper.sqlEscape(lemma));
            writer.write(", ");
            writer.write(StringHelper.sqlEscape(lemma));
            writer.write(")");

            if (words == null) {
                writer.write(";\n");
                return;
            }
            for (String word : words) {
                writer.write(", (");
                writer.write(StringHelper.sqlEscape(lemma));
                writer.write(", ");
                writer.write(StringHelper.sqlEscape(word));
                writer.write(")");
            }
            writer.write(";\n");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void setBaseFileName(String baseFileName) {
        try {
            writer = new BufferedWriter(new FileWriter(new File(baseFileName + "_one_table.sql")));
            writerCreate = new BufferedWriter(new FileWriter(new File(baseFileName + "_one_table_create.sql")));
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
