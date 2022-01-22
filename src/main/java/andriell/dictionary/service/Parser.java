package andriell.dictionary.service;

import andriell.dictionary.file.AffLinesPfx;
import andriell.dictionary.file.AffLinesSfx;
import andriell.dictionary.helpers.StringHelper;
import andriell.dictionary.writer.Writer;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

public class Parser implements Runnable {
    String baseFileName;
    File fileDic;
    File fileAff;

    String charset = "KOI8-R";
    boolean flagNum = false;
    int dicLine = 0;
    int dicTotal = 0;

    AffLinesPfx pfx = new AffLinesPfx();
    AffLinesSfx sfx = new AffLinesSfx();
    Writer writer;

    ProgressListener progressListener;

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
            writer.setBaseFileName(baseFileName);
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

        while ((line = reader.readLine()) != null) {
            try {
                dicLine++;
                line = line.trim();
                if ("".equals(line))
                    continue;
                int i = line.lastIndexOf('/');
                if (i == 0)
                    Log.wrn("Incorrect dic line: '" + line + "'");
                if (i < 0) {
                    if (writer != null)
                        writer.write(line, null);
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
                if (writer != null)
                    writer.write(lemma, wordsSfx);
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
        Log.flushFileLog();
        if (writer != null)
            writer.close();
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

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public interface ProgressListener {
        void onStart(int max);

        void onUpdate(int max, int position);

        void onComplete();
    }
}
