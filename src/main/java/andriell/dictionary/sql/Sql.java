package andriell.dictionary.sql;

public class Sql {
    public static String sqlEscape(String str) {
        if (str == null)
            return "NULL";
        return "'" + str.replace("'","''") + "'";
    }
}
