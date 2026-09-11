package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static void loadProperties() {
        try (InputStream stream = Util.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
