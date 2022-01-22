package andriell.dictionary.service;

public class Log {

    public static void println(String line) {
        System.out.println(line);
    }

    public static void error(Exception e) {
        e.printStackTrace();
    }
}
