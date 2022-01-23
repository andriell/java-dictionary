package andriell.dictionary.writer;

import andriell.dictionary.helpers.FileHelper;
import andriell.dictionary.service.Log;
import andriell.dictionary.sql.MultiInsert;
import andriell.dictionary.sql.MultiInsertEntity;
import andriell.dictionary.sql.Sql;

import java.io.File;
import java.io.Writer;
import java.util.Set;

public class SqlOneTableGroupWriter implements DicWriter, HaveStartingIndex, HaveSql {
    private final MultiInsert multiInsert;
    private long startingIndex = 0;
    private long groupId = 0;
    private String tableSfx;
    private boolean isInsertIgnore;
    private String baseFileName;

    public SqlOneTableGroupWriter() {
        multiInsert = new MultiInsert();
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
        return groupId;
    }

    @Override
    public void setTableSfx(String sfx) {
        tableSfx = sfx;
    }

    @Override
    public void setInsertSize(int insertSize) {
        multiInsert.setMaxSize(insertSize);
    }

    @Override
    public void setInsertIgnore(boolean ignore) {
        isInsertIgnore = ignore;
    }

    static class Entity implements MultiInsertEntity {
        long groupId;
        int isLemma;
        String word;

        public Entity(long groupId, int isLemma, String word) {
            this.groupId = groupId;
            this.isLemma = isLemma;
            this.word = word;
        }

        @Override public String toSql() {
            return "(" + groupId + ", " + isLemma + ", " + Sql.sqlEscape(word) + ")";
        }
    }

    @Override public String getName() {
        return "Sql one table group .sql";
    }

    @Override public void begin() {
        groupId = startingIndex - 1;
        try {
            Writer writer = FileHelper.makeWriter(new File(baseFileName + "_one_table_group" + tableSfx + ".sql"));
            multiInsert.setWriter(writer);
            multiInsert
                    .setBeginString("INSERT" + (isInsertIgnore ? " IGNORE " : " ") + "INTO dic_words_group" + tableSfx + " (group_id, is_lemma, word) VALUES ");

            Writer writerCreate = FileHelper
                    .makeWriter(new File(baseFileName + "_one_table_group" + tableSfx + "_create.sql"));
            writerCreate.write("CREATE TABLE `dic_words_group" + tableSfx + "` (\n"
                    + "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n"
                    + "\t`group_id` INT(11) NOT NULL,\n"
                    + "\t`is_lemma` TINYINT(4) NOT NULL,\n"
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
            groupId++;

            multiInsert.addEntity(new Entity(groupId, 1, lemma));
            if (words == null) {
                return;
            }

            for (String word : words) {
                multiInsert.addEntity(new Entity(groupId, 0, word));
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
            multiInsert.close();
        } catch (Exception e) {
            Log.error(e);
        }
    }
}
