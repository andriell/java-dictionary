package andriell.dictionary.gui;


import andriell.dictionary.Main;

import java.io.*;

public class Preferences {
    public static String LAST_USED_BOUNDS = "last_used_bounds";
    public static String LAST_USED_DIMENSION = "last_used_dimension";
    public static String LAST_USED_FOLDER_DATA_PASS = "last_used_folder_data_pass";
    public static String LAST_USED_FOLDER_DATA = "last_used_folder_data";
    public static String LAST_USED_FOLDER_KEY = "last_used_folder_key";
    public static String LAST_USED_FOLDER_SAVE = "last_used_folder_save";

    private static java.util.prefs.Preferences prefs;

    static {
        prefs = java.util.prefs.Preferences.userNodeForPackage(Main.class);
    }
    public static void put(String key, String value)
    {
        prefs.put(key, value);
    }

    public static void putSerializable(String key, Serializable value)
    {
        prefs.putByteArray(key, serialize(value));
    }

    public static String get(String key, String def)
    {
        return prefs.get(key, def);
    }

    public static Serializable getSerializable(String key, Serializable def)
    {
        prefs.getByteArray(key, null);
        if (prefs.getByteArray(key, null) == null) {
            return def;
        }
        return deserialize(prefs.getByteArray(key, null));
    }

    public static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Serializable deserialize(byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return (Serializable) is.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
