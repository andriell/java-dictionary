package andriell.dictionary.writer;

public interface HaveSql {
    void setTableSfx(String sfx);

    void setInsertSize(int insertSize);

    void setInsertIgnore(boolean ignore);
}
