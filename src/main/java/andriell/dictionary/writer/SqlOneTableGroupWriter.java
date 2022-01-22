package andriell.dictionary.writer;

import andriell.dictionary.helpers.StringHelper;
import andriell.dictionary.service.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class SqlOneTableGroupWriter implements Writer {
    private BufferedWriter writer;
    private BufferedWriter writerCreate;
    private int groupId = 0;

    @Override public String getName() {
        return "Sql one table group .sql";
    }

    @Override public void write(String lemma, Set<String> words) {
        try {
            if (writerCreate != null) {
                writerCreate.write("CREATE TABLE `dic_words_group` (\n"
                        + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
                        + "\t`group_id` INT(11) NOT NULL,\n" + "\t`is_lema` TINYINT(4) NOT NULL,\n"
                        + "\t`word` VARCHAR(255) NOT NULL,\n"
                        + "\tPRIMARY KEY (`id`)\n"
                        + ");\n");
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
            groupId++;

            writer.write("INSERT INTO dic_words_group (group_id, is_lema, word) VALUES (");
            writer.write(Integer.toString(groupId));
            writer.write(", 1, ");
            writer.write(StringHelper.sqlEscape(lemma));
            writer.write(")");

            if (words == null) {
                writer.write(";\n");
                return;
            }

            for (String word : words) {
                writer.write(", (");
                writer.write(Integer.toString(groupId));
                writer.write(", 0, ");
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
            writer = new BufferedWriter(new FileWriter(new File(baseFileName + "_one_table_group.sql")));
            writerCreate = new BufferedWriter(new FileWriter(new File(baseFileName + "_one_table_group_create.sql")));
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
