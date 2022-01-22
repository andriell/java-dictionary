package andriell.dictionary.helpers;

import java.io.File;

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
}
