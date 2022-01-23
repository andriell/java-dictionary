package andriell.dictionary.writer;

import andriell.dictionary.helpers.FileHelper;
import andriell.dictionary.service.Log;
import andriell.dictionary.sql.MultiInsert;
import andriell.dictionary.sql.MultiInsertEntity;
import andriell.dictionary.sql.Sql;

import java.io.File;
import java.io.Writer;
import java.util.Set;

public class SqlTwoTableWriter implements DicWriter {
    private final MultiInsert multiInsertLemma;
    private final MultiInsert multiInsertWord;
    private Writer writerCreate;
    private int lemmaId = 0;

    public SqlTwoTableWriter() {
        multiInsertLemma = new MultiInsert();
        multiInsertLemma.setBeginString("INSERT INTO dic_lemma (id, lemma) VALUES ");

        multiInsertWord = new MultiInsert();
        multiInsertWord.setBeginString("INSERT INTO dic_word (dic_lemma_id, word) VALUES ");
    }

    static class EntityLemma implements MultiInsertEntity {
        int id;
        String lemma;

        public EntityLemma(int id, String lemma) {
            this.id = id;
            this.lemma = lemma;
        }

        @Override public String toSql() {
            return "(" + id + ", " + Sql.sqlEscape(lemma) + ")";
        }
    }

    static class EntityWord implements MultiInsertEntity {
        int dicLemmaId;
        String word;

        public EntityWord(int dicLemmaId, String word) {
            this.dicLemmaId = dicLemmaId;
            this.word = word;
        }

        @Override public String toSql() {
            return "(" + dicLemmaId + ", " + Sql.sqlEscape(word) + ")";
        }
    }

    @Override public String getName() {
        return "Sql two table .sql";
    }


    @Override public void begin() {
        lemmaId = 0;
        try {
            writerCreate.write("CREATE TABLE `dic_lemma` (\n"
                    + "\t`id` INT(11) NOT NULL,\n"
                    + "\t`lemma` VARCHAR(255) NOT NULL,\n"
                    + "\tPRIMARY KEY (`id`)\n"
                    + ");\n");
            writerCreate.write("CREATE TABLE `dic_word` (\n"
                    + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
                    + "\t`dic_lemma_id` INT(11) NOT NULL,\n"
                    + "\t`word` VARCHAR(255) NOT NULL,\n"
                    + "\tPRIMARY KEY (`id`)\n"
                    + ");\n");
            writerCreate.flush();
            writerCreate.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void write(String lemma, Set<String> words) {
        // INSERT INTO lemma(id, word) VALUES ('lemma','lemma'), ('lemma','word1'), ('lemma','word2');
        try {
            if (lemma == null)
                return;

            lemmaId++;
            multiInsertLemma.addEntity(new EntityLemma(lemmaId, lemma));
            if (words == null) {
                return;
            }
            for (String word : words) {
                multiInsertWord.addEntity(new EntityWord(lemmaId, word));
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void setBaseFileName(String baseFileName) {
        try {
            Writer writerLemma = FileHelper.makeWriter(new File(baseFileName + "_two_table_lemma.sql"));
            multiInsertLemma.setWriter(writerLemma);
            Writer writerWord = FileHelper.makeWriter(new File(baseFileName + "_two_table_word.sql"));
            multiInsertWord.setWriter(writerWord);
            writerCreate = FileHelper.makeWriter(new File(baseFileName + "_two_table_create.sql"));
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override public void close() {
        try {
            multiInsertLemma.close();
            multiInsertWord.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
