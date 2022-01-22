package andriell.dictionary.writer;

public class Writers {
    private static Writer[] writers = new Writer[] { new LnwnWriter(), new NlnwnWriter(),
            new LtwnWriter(), };

    public static String[] getNames() {
        String[] r = new String[writers.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = writers[i].getName();
        }
        return r;
    }

    public static Writer getWriter(int i) {
        if (i < 0 || i >= writers.length)
            return writers[0];
        return writers[i];
    }
}
