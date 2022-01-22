package andriell.dictionary.service;

import andriell.dictionary.file.AffLinesPfx;
import andriell.dictionary.file.AffLinesSfx;
import andriell.dictionary.helpers.StringHelper;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

public class Parser {
    File fileDic;
    File fileAff;
    String charset = "KOI8-R";
    boolean flagNum = false;
    int dicLine = 0;
    int dicTotal = 0;

    AffLinesPfx pfx = new AffLinesPfx();
    AffLinesSfx sfx = new AffLinesSfx();

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

    public void write(String lemma, Set<String> words) {
        Log.println(lemma);
        if (words != null)
            Log.println(words.toString());
    }

    public void parse() throws IOException {
        parseAff(0);

        Log.println(sfx.toString());
        Log.println(pfx.toString());

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
                String ruleName = line.substring(i + 1);
                String[] ruleNames;
                if (flagNum) {
                    ruleNames = StringHelper.split(ruleName, ",");
                } else {
                    ruleNames = StringHelper.byChar(ruleName);
                }

                if (ruleNames == null) {
                    Log.println("Incorrect dic rule names: '" + ruleName + "'");
                    continue;
                }
                Set<String> wordsSfx = new TreeSet<>();
                for (String s : ruleNames) {
                    sfx.apply(lemma, s, wordsSfx);
                }
                Set<String> wordsPfx = new TreeSet<>();
                for (String s : ruleNames) {
                    pfx.apply(wordsSfx, s, wordsPfx);
                }
                wordsSfx.addAll(wordsPfx);
                write(lemma, wordsSfx);
            } catch (Exception e) {
                Log.error(e);
            }
        } reader.close();
        isr.close();
        fis.close();
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
                } else if (line.startsWith("FLAG ")) {
                    String flag = line.substring(5);
                    if (flag == null)
                        continue;
                    flag = flag.trim().toLowerCase();
                    flagNum = "num".equals(flag);
                } else if (line.startsWith("SFX ")) {
                    sfx.addLine(line);
                } else if (line.startsWith("PFX ")) {
                    pfx.addLine(line);
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
}
