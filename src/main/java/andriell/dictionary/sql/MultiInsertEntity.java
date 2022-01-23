package andriell.dictionary.sql;

public interface MultiInsertEntity {
    /**
     * @return String: ('value1', 10, 'value2', NULL)
     */
    String toSql();
}
