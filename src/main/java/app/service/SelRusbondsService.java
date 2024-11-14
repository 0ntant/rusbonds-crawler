package app.service;

import app.model.RusbondsKeys;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public interface SelRusbondsService
{
    RusbondsKeys getRusbondsCred();

    String getBondRating(int fintoolId);

    void makeRandomMoves();

    void createAuthSession();

    ChromeOptions setOptions();

    RemoteWebDriver createRemoteDriver(ChromeOptions options);

    void login();

    void setRusbondsKeys();

    void waitDriver(int waitSec);

    void closeSession();

    String getAccessBearerToken();

    String getRefreshToken();

    String getCookieValueSpid();

    String getCookieValueSpsc();
}
