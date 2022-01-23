package andriell.dictionary.helpers;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileHelper {
    public static File fileNearby(File file, String append) {
        if (!file.isFile()) {
            return null;
        }
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        String name = fileName.substring(0, i);
        return new File(file.getParent(), name + append);
    }


    public static Writer makeWriter(File file) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        return new BufferedWriter(osw);
    }
}
