package andriell.dictionary.file;


import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

public class AffLine {
    String name;
    HashSet<Rule> rules;


    public static class Rule {
        String f; // From
        String t; // To
        Pattern p; // Pattern

        public boolean matches(String lemma) {
            return p.matcher(lemma).matches();
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Rule rule = (Rule) o;
            return Objects.equals(f, rule.f) && Objects.equals(t, rule.t) && Objects
                    .equals(p, rule.p);
        }

        @Override public int hashCode() {
            return Objects.hash(f, t, p);
        }

        @Override public String toString() {
            return "{'f':'" + f + '\'' + ",'t':'" + t + "', 'p':'"  + p + "'}";
        }
    }

    @Override public String toString() {
        return "{'name':'" + name + "', 'rules':[" + rules + "]}";
    }
}
