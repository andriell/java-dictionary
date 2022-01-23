package andriell.dictionary.writer;

import andriell.dictionary.helpers.FileHelper;
import andriell.dictionary.service.Log;
import andriell.dictionary.sql.MultiInsert;
import andriell.dictionary.sql.MultiInsertEntity;
import andriell.dictionary.sql.Sql;

import java.io.File;
import java.io.Writer;
import java.util.Set;

public class SqlOneTableWriter implements DicWriter {
    private final MultiInsert multiInsert;
    private Writer writerCreate;

    public SqlOneTableWriter() {
        multiInsert = new MultiInsert();
        multiInsert.setBeginString("INSERT INTO dic_words (lemma, word) VALUES ");
    }

    static class Entity implements MultiInsertEntity {
        String lemma;
        String word;

        public Entity(String lemma, String word) {
            this.lemma = lemma;
            this.word = word;
        }

        @Override public String toSql() {
            return "(" + Sql.sqlEscape(lemma) + ", " + Sql.sqlEscape(word) + ")";
        }
    }

    @Override public String getName() {
        return "Sql one table .sql";
    }

    @Override public void setBaseFileName(String baseFileName) {
        try {
            Writer writer = FileHelper.makeWriter(new File(baseFileName + "_one_table.sql"));
            multiInsert.setWriter(writer);
            writerCreate = FileHelper.makeWriter(new File(baseFileName + "_one_table_create.sql"));
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void begin() {
        try {
            writerCreate.write("CREATE TABLE `dic_words` (\n"
                    + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
                    + "\t`lemma` VARCHAR(255) NULL DEFAULT NULL,\n"
                    + "\t`word` VARCHAR(255) NULL DEFAULT NULL,\n"
                    + "\tPRIMARY KEY (`id`)\n"
                    + ");\n");
            writerCreate.flush();
            writerCreate.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void write(String lemma, Set<String> words) {
        try {
            if (lemma == null)
                return;
            multiInsert.addEntity(new Entity(lemma, lemma));

            if (words == null) {
                return;
            }
            for (String word : words) {
                multiInsert.addEntity(new Entity(lemma, word));
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void close() {
        try {
            multiInsert.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
