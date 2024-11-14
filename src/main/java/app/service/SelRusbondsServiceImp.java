package app.service;

import app.model.RusbondsKeys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import static app.config.SeleniumConfig.*;
import static app.config.RusbondsConfig.*;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Random;

@Slf4j
public class SelRusbondsServiceImp implements SelRusbondsService {
    RemoteWebDriver driver;
    @Getter
    private String accessBearerToken;
    @Getter
    private String refreshToken;
    @Getter
    private String cookieValueSpid;
    @Getter
    private String cookieValueSpsc;

    private String userAgent;

    @Override
    public RusbondsKeys getRusbondsCred()
    {
        return new RusbondsKeys(
                cookieValueSpid,
                cookieValueSpsc,
                accessBearerToken,
                userAgent,refreshToken
        );
    }

    @Override
    public String getBondRating(int fintoolId)
    {

        SessionId sessionId = driver.getSessionId();
        if(sessionId == null)
        {
            createAuthSession();
        }

        String url = String.format("https://rusbonds.ru/bonds/%s/", fintoolId);
        driver.get(url);
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofMinutes(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        String rating = getRatingValue();
        setRusbondsKeys();
        return rating;
    }

    protected String getRatingValue()
    {
        String rating = "***"; //hidden rating default
        List<WebElement> ratingContainer = driver.findElements(By.className("ratings"));
        if (!ratingContainer.isEmpty())
        {
            rating = ratingContainer
                    .get(0)
                    .findElements(By.tagName("span"))
                    .get(1)
                    .getText();
        }
        return rating;
    }

    @Override
    public void makeRandomMoves()
    {
        Actions actions = new Actions(driver);
        Random random = new Random();
        String[] url = {
                "https://rusbonds.ru/filters/bonds",
                "https://rusbonds.ru/analytics",
                "https://rusbonds.ru/portfolio",
                "https://rusbonds.ru/companies/",
                "https://rusbonds.ru/rankings",
                "https://rusbonds.ru/markets/main/otc",
                "https://rusbonds.ru/seminars",
                "https://rusbonds.ru/tariffs"
        };

        for (int i=0; i < random.nextInt(url.length) ; i++)
        {
            actions.moveToElement(driver.findElements(By.className("top-bar__menu")).get(0)).moveByOffset(5, 10);
            waitDriver(random.nextInt(5, 10));
            actions.moveToElement(driver.findElements(By.className("top-bar__menu")).get(0)).moveByOffset(10, 90);
            waitDriver(random.nextInt(5, 10));
            driver.get(url[random.nextInt(url.length)]);
            waitDriver(random.nextInt(15, 29));
            actions.moveToElement(driver.findElements(By.className("top-bar__menu")).get(0))
                    .moveByOffset(random.nextInt(30), random.nextInt(10));
        }
    }

    @Override
    public void createAuthSession()
    {
        driver = createRemoteDriver(setOptions());
        login();
    }

    @Override
    public ChromeOptions setOptions()
    {
        return new ChromeOptions();
    }

    @Override
    public RemoteWebDriver createRemoteDriver(ChromeOptions options)
    {

        try
        {
            URL remoteDriverUrl = new URL(SELENIUM_URL);
            return new RemoteWebDriver(remoteDriverUrl, options);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void login()
    {
        try
        {
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofMinutes(2));
            driver.manage().window().maximize();
            driver.get("https://rusbonds.ru/login");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

            List<WebElement> inputFields = driver.findElements(By.tagName("input"));
            List<WebElement> buttons = driver.findElements(By.tagName("button"));

            inputFields.get(0).sendKeys(LOGIN);
            inputFields.get(1).sendKeys(PASSWORD);
            buttons.get(1).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sign-out")));
            waitDriver(10);
            setRusbondsKeys();
            log.info("Authorisation success");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            closeSession();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setRusbondsKeys()
    {
        this.refreshToken = getItem("auth._refresh_token.local");
        this.accessBearerToken = getItem("auth._token.local");
        this.cookieValueSpid = driver.manage().getCookieNamed("spid").getValue();
        this.cookieValueSpsc = driver.manage().getCookieNamed("spsc").getValue();
        this.userAgent = getUserAgent();
    }

    private String getUserAgent()
    {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        return  (String) jsExecutor.executeScript("return navigator.userAgent;");
    }

    private String getItem(String itemName)
    {
        JavascriptExecutor js = ((JavascriptExecutor)driver);
        return (String) js.executeScript(String.format(
                "return window.localStorage.getItem('%s');", itemName));
    }

    @Override
    public void waitDriver(int waitSec)
    {
        synchronized (Thread.currentThread())
        {
            try
            {
                Thread.currentThread().wait(1000 * waitSec);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                log.error(e.getMessage());
                closeSession();
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void closeSession()
    {
        driver.quit();
        log.info("Session closed");
    }
}
