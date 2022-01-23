package andriell.dictionary.writer;

import andriell.dictionary.helpers.FileHelper;
import andriell.dictionary.service.Log;
import andriell.dictionary.sql.MultiInsert;
import andriell.dictionary.sql.MultiInsertEntity;
import andriell.dictionary.sql.Sql;

import java.io.File;
import java.io.Writer;
import java.util.Set;

public class SqlTwoTableWriter implements DicWriter, HaveStartingIndex, HaveLemmeLemma, HaveSql {
    private final MultiInsert multiInsertLemma;
    private final MultiInsert multiInsertWord;
    private boolean isLemmaLemma = false;
    private long startingIndex = 0;
    private long lemmaId = 0;
    private String tableSfx;
    private boolean isInsertIgnore;
    private String baseFileName;

    public SqlTwoTableWriter() {
        multiInsertLemma = new MultiInsert();

        multiInsertWord = new MultiInsert();
    }

    @Override
    public long getStartingIndex() {
        return startingIndex;
    }

    @Override
    public void setStartingIndex(long index) {
        startingIndex = index;
    }

    @Override
    public long getLastIndex() {
        return lemmaId;
    }

    @Override
    public void setLemmeLemma(boolean b) {
        isLemmaLemma = b;
    }

    @Override
    public void setTableSfx(String sfx) {
        tableSfx = sfx;
    }

    @Override
    public void setInsertSize(int insertSize) {
        multiInsertLemma.setMaxSize(insertSize);
        multiInsertWord.setMaxSize(insertSize);
    }

    @Override
    public void setInsertIgnore(boolean ignore) {
        isInsertIgnore = ignore;
    }

    static class EntityLemma implements MultiInsertEntity {
        long id;
        String lemma;

        public EntityLemma(long id, String lemma) {
            this.id = id;
            this.lemma = lemma;
        }

        @Override public String toSql() {
            return "(" + id + ", " + Sql.sqlEscape(lemma) + ")";
        }
    }

    static class EntityWord implements MultiInsertEntity {
        long dicLemmaId;
        String word;

        public EntityWord(long dicLemmaId, String word) {
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
        lemmaId = startingIndex - 1;
        try {
            Writer writerLemma = FileHelper.makeWriter(new File(baseFileName + "_two_table_lemma" + tableSfx + ".sql"));
            multiInsertLemma.setWriter(writerLemma);
            multiInsertLemma.setBeginString("INSERT" + (isInsertIgnore ? " IGNORE " : " ") + "INTO dic_lemma" + tableSfx + " (id, lemma) VALUES ");

            Writer writerWord = FileHelper.makeWriter(new File(baseFileName + "_two_table_word" + tableSfx + ".sql"));
            multiInsertWord.setWriter(writerWord);
            multiInsertWord.setBeginString("INSERT" + (isInsertIgnore ? " IGNORE " : " ") + "INTO dic_word" + tableSfx + " (dic_lemma_id, word) VALUES ");

            Writer writerCreate = FileHelper.makeWriter(new File(baseFileName + "_two_table" + tableSfx + "_create.sql"));
            writerCreate.write("CREATE TABLE `dic_lemma" + tableSfx + "` (\n"
                    + "\t`id` INT(11) NOT NULL,\n"
                    + "\t`lemma` VARCHAR(255) NOT NULL,\n"
                    + "\tPRIMARY KEY (`id`)\n"
                    + ");\n");
            writerCreate.write("CREATE TABLE `dic_word" + tableSfx + "` (\n"
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
            if (isLemmaLemma || words == null) {
                multiInsertWord.addEntity(new EntityWord(lemmaId, lemma));
            }
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
        this.baseFileName = baseFileName;
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
