package com.tests.myopenstack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    public static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                logger.error(STR."Sorry, unable to find \{fileName}");
                return null;
            }
            properties.load(input);
        } catch (IOException ex) {
            logger.error(STR."Error reading properties file: \{fileName}", ex);
        }
        return properties;
    }
}
