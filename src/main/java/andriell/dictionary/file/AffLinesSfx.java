package andriell.dictionary.file;

import java.util.regex.Pattern;

public class AffLinesSfx extends AffLines {
    @Override Pattern makePattern(String s) {
        return Pattern.compile(".*" + s + "$");
    }

    String apply(String lemma, AffLine.Rule rule) {
        if (!lemma.endsWith(rule.f)) {
            return null;
        }
        if (!rule.matches(lemma)) {
            return null;
        }

        String word = lemma.substring(0, lemma.length() - rule.f.length()) + rule.t;
        return word;
    }
}
