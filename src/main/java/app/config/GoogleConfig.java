package app.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GoogleConfig
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

    public static final String USER = appProps.getProperty("user");
    public static final String CREDENTIALS_FILE_PATH = appProps.getProperty("client.secret.path");
    public static final String APPLICATION_NAME = appProps.getProperty("app.name");;
    public static final String SPREADSHEET_ID = appProps.getProperty("app.id");
}
