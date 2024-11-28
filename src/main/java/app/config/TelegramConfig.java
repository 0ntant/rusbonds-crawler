package app.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TelegramConfig
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
    public static final String ACCESS_TOKEN = appProps.getProperty("telegram.bot.access_token");
    public static final String[] CLIENTS = appProps.getProperty("telegram.bot.clients").split(",");
    public static final String[] TESTERS = appProps.getProperty("telegram.bot.testers").split(",");
    public static final String BOT_USERNAME = appProps.getProperty("telegram.bot.username");
}
