package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();

    private final Properties properties = new Properties();

    private Config() {
        loadAllProperties();
    }

    private void loadAllProperties() {
        String[] configFiles = {
                "config.properties",
                "model-comparison.properties"
        };

        for (String fileName : configFiles) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (is != null) {
                    properties.load(is);
                    System.out.println("✅ Конфигурация загружена: " + fileName);
                } else {
                    System.err.println("❌ Файл не найден в resources: " + fileName);
                }
            } catch (IOException e) {
                throw new RuntimeException("Критическая ошибка при чтении файла: " + fileName, e);
            }
        }
    }

    public static String getProperty(String key) {
        return INSTANCE.properties.getProperty(key);
    }
}
