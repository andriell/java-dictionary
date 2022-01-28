package andriell.dictionary.service;

import andriell.dictionary.file.AffLinesPfx;
import andriell.dictionary.file.AffLinesSfx;
import andriell.dictionary.helpers.StringHelper;
import andriell.dictionary.writer.DicWriter;
import andriell.dictionary.writer.HaveStartingIndex;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

public class Parser implements Runnable {
    public static final String BOM = "О╩©";
    String baseFileName;
    File fileDic;
    File fileAff;

    String charset = "KOI8-R";
    boolean flagNum = false;
    int dicLine = 0;
    int dicTotal = 0;

    AffLinesPfx pfx = new AffLinesPfx();
    AffLinesSfx sfx = new AffLinesSfx();
    DicWriter dicWriter;

    ProgressListener progressListener;
    CompleteListener completeListener;

    public File getFileDic() {
        return fileDic;
    }

    public void setFileDic(File fileDic) {
        this.fileDic = fileDic;
        if (fileDic.isFile()) {
            baseFileName = fileDic.getAbsolutePath();
            int i = baseFileName.lastIndexOf('.');
            baseFileName = baseFileName.substring(0, i);
            fileAff = new File(baseFileName + ".aff");
            File fileLog = new File(baseFileName + ".log");
            Log.setFileLog(fileLog);
            dicWriter.setBaseFileName(baseFileName);
        }
    }

    public File getFileAff() {
        return fileAff;
    }

    public void parse() throws IOException {
        (new Thread(this)).start();
    }

    @Override public void run() {
        try {
            parseAff(0);
        } catch (Exception e) {
            Log.error(e);
        }

        // Log.info(sfx.toString());
        // Log.info(pfx.toString());
        try {
            parseDic();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private void parseDic() throws IOException {
        FileInputStream fis = new FileInputStream(fileDic);
        InputStreamReader isr = new InputStreamReader(fis, charset);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        dicLine = 0;
        if ((line = reader.readLine()) != null) {
            try {
                if (line.startsWith(BOM))
                    line = line.substring(3);
                dicTotal = Integer.parseInt(line);
            } catch (Exception e) {
                Log.error(e);
            }
        }

        try {
            if (progressListener != null)
                progressListener.onStart(dicTotal);
        } catch (Exception e) {
            Log.error(e);
        }

        if (dicWriter != null)
            dicWriter.begin();

        long wordsCount = 0;

        //<editor-fold desc="Order">
        Set<String> lines = new TreeSet<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line.trim());
        }
        //</editor-fold>

        for (String str: lines) {
            try {
                dicLine++;
                if ("".equals(str))
                    continue;
                int i = str.lastIndexOf('/');
                if (i == 0)
                    Log.wrn("Incorrect dic line: '" + str + "'");
                if (i < 0) {
                    if (dicWriter != null) {
                        dicWriter.write(str, null);
                        wordsCount++;
                    }
                    continue;
                }
                String lemma = str.substring(0, i);
                String ruleName = str.substring(i + 1);
                String[] ruleNames;
                if (flagNum) {
                    ruleNames = StringHelper.split(ruleName, ",");
                } else {
                    ruleNames = StringHelper.byChar(ruleName);
                }

                if (ruleNames == null) {
                    Log.wrn("Incorrect dic rule names: '" + ruleName + "'");
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
                if (dicWriter != null) {
                    dicWriter.write(lemma, wordsSfx);
                    wordsCount += wordsSfx.size();
                }
                try {
                    if (progressListener != null)
                        progressListener.onUpdate(dicTotal, dicLine);
                } catch (Exception e) {
                    Log.error(e);
                }
            } catch (Exception e) {
                Log.error(e);
            }
        }
        try {
            if (progressListener != null)
                progressListener.onComplete();
        } catch (Exception e) {
            Log.error(e);
        }
        reader.close();
        isr.close();
        fis.close();
        Log.info("Words count: " + wordsCount);
        if (dicWriter != null) {
            dicWriter.close();
            if (completeListener != null) {
                long lastIndex = (dicWriter instanceof HaveStartingIndex) ? ((HaveStartingIndex) dicWriter).getLastIndex() : -1;
                completeListener.onComplete(wordsCount, lastIndex);
            }
        }
        Log.flushFileLog();
    }

    private void parseAff(int skip) throws IOException {
        FileInputStream fis = new FileInputStream(fileAff);
        InputStreamReader isr = new InputStreamReader(fis, charset);
        BufferedReader reader = new BufferedReader(isr);
        String line;
        int i = 0;
        pfx.clear();
        sfx.clear();
        while ((line = reader.readLine()) != null) {
            if (i == 0 && line.startsWith(BOM))
                line = line.substring(3);
            i++;
            if (skip >= i)
                continue;
            try {
                if (line.startsWith("SET ")) {
                    charset = line.substring(4);
                    Log.info("Set charset: '" + charset + "'");
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
                    Log.info("Skip aff line: '" + line + "'");
                }

            } catch (Exception e) {
                Log.error(e);
            }
        }
        reader.close();
        isr.close();
        fis.close();
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public CompleteListener getCompleteListener() {
        return completeListener;
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public DicWriter getDicWriter() {
        return dicWriter;
    }

    public void setDicWriter(DicWriter dicWriter) {
        this.dicWriter = dicWriter;
    }

    public interface ProgressListener {
        void onStart(int max);

        void onUpdate(int max, int position);

        void onComplete();
    }

    public interface CompleteListener {
        void onComplete(long totalWords, long lastIndex);
    }
}
