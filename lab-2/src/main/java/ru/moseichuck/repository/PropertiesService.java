package ru.moseichuck.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {

    private static final String PROPERTIES_FILE = "/application.properties";

    private final Properties properties;

    public PropertiesService() {
        properties = new Properties();
        try (InputStream inputStream = PropertiesService.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("В ресурсах не найден " + PROPERTIES_FILE);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать " + PROPERTIES_FILE, e);
        }
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("В " + PROPERTIES_FILE + " не задано значение для ключа " + key);
        }
        return value;
    }
}
