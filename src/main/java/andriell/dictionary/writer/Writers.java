package andriell.dictionary.writer;

public class Writers {
    private static DicWriter[] dicWriters = new DicWriter[] {
            new LnwnWriter(),
            new NlnwnWriter(),
            new LtwnWriter(),
            new SqlOneTableWriter(),
            new SqlTwoTableWriter(),
            new SqlOneTableGroupWriter(),
    };

    public static String[] getNames() {
        String[] r = new String[dicWriters.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = dicWriters[i].getName();
        }
        return r;
    }

    public static DicWriter getWriter(int i) {
        if (i < 0 || i >= dicWriters.length)
            return dicWriters[0];
        return dicWriters[i];
    }
}
