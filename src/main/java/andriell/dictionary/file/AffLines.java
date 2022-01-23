package andriell.dictionary.file;

import andriell.dictionary.helpers.StringHelper;
import andriell.dictionary.service.Log;

import java.util.*;
import java.util.regex.Pattern;

public abstract class AffLines {
    Map<String, AffLine> map = new HashMap<>();

    abstract Pattern makePattern(String s);

    abstract String apply(String lemma, AffLine.Rule rule);

    public void apply(Collection<String> lemmas, String ruleName, Set<String> set) {
        for (String lemma:lemmas) {
            apply(lemma, ruleName, set);
        }
    }

    public void apply(String lemma, String ruleName, Set<String> set) {
        AffLine affLine = map.get(ruleName);
        if (affLine == null)
            return;

        for (AffLine.Rule rule : affLine.rules) {
            String word = apply(lemma, rule);
            if (word != null)
                set.add(word);
        }
    }

    /**
     * SFX S Y  40
     * SFX S ый ы [^н]ый
     * SFX S ый о [^н]ый
     * @param s - SFX S ый ы [^н]ый
     */
    public void addLine(String s) {
        String[] strings = StringHelper.split(s, "\\s+");
        String ruleName = strings[1];
        AffLine line = map.get(ruleName);
        if (line == null) {
            if (strings.length < 4) {
                Log.wrn("Incorrect first aff line: '" + s + "'");
                return;
            }
            line = new AffLine();
            line.name = ruleName;
            int i = Integer.parseInt(strings[3]);
            line.rules = new HashSet<>(i);
            map.put(ruleName, line);
            return;
        }
        if (strings.length < 5) {
            Log.wrn("Incorrect aff line: '" + s + "'");
            return;
        }
        AffLine.Rule rule = new AffLine.Rule();
        rule.f = "0".equals(strings[2]) ? "" : strings[2];
        rule.t = "0".equals(strings[3]) ? "" : strings[3];
        if (rule.f.equals(rule.t)) {
            Log.wrn("Duplicate aff line: '" + s + "'");
            return;
        }
        rule.p = makePattern(strings[4]);

        line.rules.add(rule);
    }

    public void clear() {
        map.clear();
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (AffLine affLine : map.values()) {
            sb.append(affLine.toString());
        }
        return sb.toString();
    }
}
