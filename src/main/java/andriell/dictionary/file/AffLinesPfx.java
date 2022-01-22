package andriell.dictionary.file;

import java.util.regex.Pattern;

public class AffLinesPfx extends AffLines {
    @Override Pattern makePattern(String s) {
        return Pattern.compile("^" + s + ".*");
    }

    @Override String apply(String lemma, AffLine.Rule rule) {
        if (!lemma.startsWith(rule.f)) {
            return null;
        }
        if (!rule.matches(lemma)) {
            return null;
        }

        String word = rule.t + lemma.substring(rule.f.length());
        return word;
    }
}
