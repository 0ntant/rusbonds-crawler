package app.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SeleniumConfig
{
    private static final Properties appProps = new Properties();
    static
    {
        try
        {
            appProps.load(new FileInputStream("config.properties"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String SELENIUM_URL = appProps.getProperty("selenium.url");
}
