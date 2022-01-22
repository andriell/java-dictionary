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
}
