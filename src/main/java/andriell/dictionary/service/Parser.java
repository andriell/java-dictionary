package andriell.dictionary.service;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

public class Parser {
    File fileDic;
    File fileAff;
    String charset = "KOI8-R";

    public File getFileDic() {
        return fileDic;
    }

    public void setFileDic(File fileDic) {
        this.fileDic = fileDic;
        if (fileDic.isFile()) {
            int i = fileDic.getName().lastIndexOf('.');
            String name = fileDic.getName().substring(0,i);
            fileAff = new File(fileDic.getParent(), name + ".aff");
        }
    }

    public File getFileAff() {
        return fileAff;
    }

    public void parse() throws IOException {
        parseAff(0);
        for (PfxSfx pfxSfx: pfxSfxMap.values()) {
            Log.println(pfxSfx.toString());
        }
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
                    addSFX(line, true);
                } else if (line.startsWith("PFX ")) {
                    addSFX(line, false);
                } else if (!"".equals(line)) {
                    Log.println("Skip line: '" + line + "'");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        reader.close();
        isr.close();
        fis.close();
    }

    HashMap<String, PfxSfx> pfxSfxMap = new HashMap<>();

    private void addSFX(String s, boolean isSfx) {
        String[] strings = s.split("\\s+");
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
