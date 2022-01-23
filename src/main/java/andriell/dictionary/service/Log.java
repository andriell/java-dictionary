package andriell.dictionary.service;

import andriell.dictionary.helpers.FileHelper;

import java.io.*;

public class Log {
    private static Writer writer;

    public static void setFileLog(File fileLog) {
        try {
            writer = FileHelper.makeWriter(fileLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeFileLog() {
        try {
            if (writer == null)
                return;
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flushFileLog() {
        try {
            if (writer == null)
                return;
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void info(String line) {
        try {
            if (writer == null)
                return;
            writer.write("Info: ");
            writer.write(line);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void wrn(String line) {
        try {
            if (writer == null)
                return;
            writer.write("Warning: ");
            writer.write(line);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void error(Exception e) {
        try {
            if (writer == null)
                return;
            writer.write("Error: ");
            writer.write(e.getMessage());
            writer.write("\n");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


}
