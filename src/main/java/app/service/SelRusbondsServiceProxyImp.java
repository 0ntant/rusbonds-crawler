package app.service;

import app.mapper.RusbondMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Slf4j
public class SelRusbondsServiceProxyImp
        extends SelRusbondsServiceImp
        implements SelRusbondsServiceProxy
{
    BrowserMobProxy proxy;

    public SelRusbondsServiceProxyImp()
    {
        super();
        configProxy();
    }

    private void configProxy()
    {
        proxy = new BrowserMobProxyServer();
        proxy.setTrustAllServers(true);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

        proxy.start(8080);
    }

    @Override
    public void createAuthSession()
    {
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        ChromeOptions options = new ChromeOptions();

        options.setProxy(seleniumProxy);
        options.setAcceptInsecureCerts(true);

        driver = super.createRemoteDriver(options);
        login();
    }

    @Override
    public String getBondRating(int fintoolId)
    {

        SessionId sessionId = driver.getSessionId();
        if(sessionId == null)
        {
            createAuthSession();
        }
        proxy.newHar("remote_test");
        String url = String.format("https://rusbonds.ru/bonds/%s/", fintoolId);
        driver.get(url);

        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofMinutes(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        String rating = getRatingValue();

        if (rating.contains("*"))
        {
            log.warn("Source html hidden fintoolID={} search rest request",
                    fintoolId
            );
            rating  = getRatingValueFromRest();
        }

        waitDriver(45);
        return rating;
    }

    String getRatingValueFromRest()
    {
        List<HarEntry> entries = proxy.getHar().getLog().getEntries();
        String risksUrlPart = "/risks";
        ObjectMapper objectMapper = new ObjectMapper();
        for (HarEntry entry : entries)
        {
            if (entry.getRequest().getUrl().contains(risksUrlPart))
            {
                try
                {
                    JsonNode jsonRisks = objectMapper.readTree(entry.getResponse().getContent().getText());
                    return RusbondMapper.ratingRu(jsonRisks);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    throw  new RuntimeException(ex);
                }
            }
        }
        throw new RuntimeException(String.format("URL part=%s NOT_FOUND",risksUrlPart));
    }

    @Override
    public void stopProxy() {
        if (proxy.isStarted())
        {
            proxy.stop();
        }
    }
}
