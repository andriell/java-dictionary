package andriell.dictionary.helpers;

public class StringHelper {
    public static String[] split(String s, String regEex) {
        if (s == null || regEex == null)
            return null;
        String[] strings = s.split(regEex);
        int i = 0;
        for (String s1: strings) {
            if (!"".equals(s1)) {
                i++;
            }
        }
        if (i == 0)
            return null;
        if (i == strings.length)
            return strings;
        String[] r = new String[i];
        i = 0;
        for (String s1: strings) {
            if (!"".equals(s1)) {
                r[i] = s1;
                i++;
            }
        }
        return r;
    }

    public static String[] byChar(String str) {
        if (str == null)
            return null;

        String[] r = new String[str.length()];
        for (int i = 0; i < str.length(); i++) {
            r[i] = Character.toString(str.charAt(i));
        }
        return r;
    }

    public static String sqlEscape(String str) {
        if (str == null)
            return "NULL";
        return "'" + str.replace("'","''") + "'";
    }
}
