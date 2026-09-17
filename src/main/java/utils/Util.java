package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {
    private static final Properties PROPERTIES = loadProperties();

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream stream = Util.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (stream == null) {
                throw new RuntimeException("application.properties not found");
            }
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
