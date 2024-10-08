package app.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RusbondsConfig
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

    public static  String COOKIE_SPID = appProps.getProperty("rusbonds.cookie.spid");;
    public static  String COOKIE_SPSC = appProps.getProperty("rusbonds.cookie.spsc");;

    public static final String LOGIN = appProps.getProperty("rusbonds.login");
    public static final String PASSWORD = appProps.getProperty("rusbonds.password");
}
