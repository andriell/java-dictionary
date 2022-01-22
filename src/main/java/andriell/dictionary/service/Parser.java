package andriell.dictionary.service;

import andriell.dictionary.helpers.StringHelper;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Parser {
    File fileDic;
    File fileAff;
    String charset = "KOI8-R";
    int dicLine = 0;
    int dicTotal = 0;

    HashMap<String, PfxSfx> pfxSfxMap = new HashMap<>();

    public File getFileDic() {
        return fileDic;
    }

    public void setFileDic(File fileDic) {
        this.fileDic = fileDic;
        if (fileDic.isFile()) {
            int i = fileDic.getName().lastIndexOf('.');
            String name = fileDic.getName().substring(0, i);
            fileAff = new File(fileDic.getParent(), name + ".aff");
        }
    }

    public File getFileAff() {
        return fileAff;
    }

    public void write(String lemma, List<String> words) {
        Log.println(lemma);
        Log.println(words.toString());
    }

    public void parse() throws IOException {
        parseAff(0);

        for (PfxSfx pfxSfx : pfxSfxMap.values()) {
            Log.println(pfxSfx.toString());
        }

        parseDic();
    }

    private void parseDic() throws IOException {
        FileInputStream fis = new FileInputStream(fileDic);
        InputStreamReader isr = new InputStreamReader(fis, charset);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        dicLine = 0;
        if ((line = reader.readLine()) != null) {
            try {
                dicTotal = Integer.parseInt(line);
            } catch (Exception e) {
                Log.error(e);
            }
        }
        while ((line = reader.readLine()) != null) {
            try {
                dicLine++;
                line = line.trim();
                if ("".equals(line))
                    continue;
                int i = line.lastIndexOf('/');
                if (i == 0)
                    Log.println("Incorrect dic line: '" + line + "'");
                if (i < 0) {
                    write(line, null);
                    continue;
                }
                String lemma = line.substring(0, i);
                String pfxSfxStr = line.substring(i + 1);
                String[] pfxSfxNames = StringHelper.split(pfxSfxStr, ",");
                ;
                if (pfxSfxNames == null) {
                    Log.println("Incorrect dic pfxSfxNames: '" + pfxSfxStr + "'");
                    continue;
                }
                List<String> words = new ArrayList<>();
                for (String pfxSfxName : pfxSfxNames) {
                    List<String> words1 = applyPfxSfx(lemma, pfxSfxName);
                    if (words1 != null)
                        words.addAll(words1);
                }
                write(lemma, words);
            } catch (Exception e) {
                Log.error(e);
            }
        } reader.close();
        isr.close();
        fis.close();
    }

    private List<String> applyPfxSfx(String lemma, String pfxSfxName) {
        PfxSfx pfxSfx = pfxSfxMap.get(pfxSfxName);
        if (pfxSfx == null)
            return null;

        List<String> r = new ArrayList<>(pfxSfx.rules.size());
        for (PfxSfx.Rule rule : pfxSfx.rules) {
            if (!rule.matches(lemma)) {
                continue;
            }
            if (pfxSfx.isSfx && lemma.endsWith(rule.f)) {
                String word = lemma.substring(0, -1 * rule.f.length()) + rule.t;
                r.add(word);
            } else if (!pfxSfx.isSfx && lemma.startsWith(rule.f)) {
                String word = rule.t + lemma.substring(rule.f.length());
                r.add(word);
            }
        }
        return r;
    }

    private void parseAff(int skip) throws IOException {
        FileInputStream fis = new FileInputStream(fileAff);
        InputStreamReader isr = new InputStreamReader(fis, charset);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            i++;
            if (skip >= i)
                continue;
            try {
                if (line.startsWith("SET ")) {
                    charset = line.substring(4);
                    Log.println("Set charset: '" + charset + "'");
                    parseAff(i);
                    break;
                } else if (line.startsWith("SFX ")) {
                    addPfxSfx(line, true);
                } else if (line.startsWith("PFX ")) {
                    addPfxSfx(line, false);
                } else if (!"".equals(line)) {
                    Log.println("Skip line: '" + line + "'");
                }

            } catch (Exception e) {
                Log.error(e);
            }
        }
        reader.close();
        isr.close();
        fis.close();
    }

    private void addPfxSfx(String s, boolean isSfx) {
        String[] strings = StringHelper.split(s, "\\s+");
        String name = strings[1];
        PfxSfx pfxSfx = pfxSfxMap.get(name);
        if (pfxSfx == null) {
            if (strings.length < 4) {
                Log.println("Incorrect line: '" + s + "'");
                return;
            }
            pfxSfx = new PfxSfx();
            pfxSfx.isSfx = isSfx;
            pfxSfx.name = name;
            int i = Integer.parseInt(strings[3]);
            pfxSfx.rules = new HashSet<>(i);
            pfxSfxMap.put(name, pfxSfx);
            return;
        }
        if (strings.length < 5) {
            Log.println("Incorrect line: '" + s + "'");
            return;
        }
        PfxSfx.Rule rule = new PfxSfx.Rule();
        rule.f = "0".equals(strings[2]) ? "" : strings[2];
        rule.t = "0".equals(strings[3]) ? "" : strings[3];
        if (rule.f.equals(rule.t)) {
            Log.println("Duplicate line: '" + s + "'");
            return;
        }
        if (isSfx) {
            rule.p = Pattern.compile(strings[4] + "$");
        } else {
            rule.p = Pattern.compile("^" + strings[4]);
        }
        pfxSfx.rules.add(rule);
    }

    static class PfxSfx {
        boolean isSfx;
        String name;
        HashSet<Rule> rules;

        static class Rule {
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
            return "{'is_sfx':'" + isSfx + "', 'name':'" + name + "', 'rules':[" + rules + "]}";
        }
    }
}
