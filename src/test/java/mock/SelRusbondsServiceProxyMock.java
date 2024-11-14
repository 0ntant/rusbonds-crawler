package mock;

import app.model.RusbondsKeys;
import app.service.SelRusbondsServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Random;

@Slf4j
public class SelRusbondsServiceProxyMock
        implements SelRusbondsServiceProxy
{
    double waitTime;
    RatingProvider ratingProvider = new RatingProvider();
    ExceptionProvider exceptionProvider = new ExceptionProvider();

    public SelRusbondsServiceProxyMock(double waitTime)
    {
        this.waitTime = waitTime;
    }

    @Override
    public void createAuthSession()
    {
        log.info("MOCK {} createAuthSession", this.getClass());
        waitSeconds(waitTime);
    }

    @Override
    public ChromeOptions setOptions()
    {
        log.info("MOCK {} setOptions", this.getClass());
        return new ChromeOptions();
    }

    @Override
    public RemoteWebDriver createRemoteDriver(ChromeOptions options)
    {
        log.info("MOCK {} createRemoteDriver", this.getClass());
        return new RemoteWebDriver(options);
    }

    @Override
    public void login()
    {
        log.info("MOCK {} login", this.getClass());
        waitSeconds(waitTime);
    }

    @Override
    public void setRusbondsKeys()
    {
        log.info("MOCK {} setRusbondsKeys", this.getClass());
        waitSeconds(waitTime);
    }

    @Override
    public void waitDriver(int waitSec)
    {
        log.info("MOCK {} waitDriver arg={}", this.getClass(), waitSec);
        waitSeconds(waitTime);
    }

    @Override
    public void closeSession()
    {
        log.info("MOCK {} closeSession", this.getClass());
    }

    @Override
    public String getAccessBearerToken()
    {
        String token = "MOCK_TOKEN_%s".formatted(this.getClass());
        log.info("MOCK {} getAccessBearerToken return token={}",
                this.getClass(),
                token
        );
        return token;
    }

    @Override
    public String getRefreshToken()
    {
        String token = "MOCK_TOKEN_%s".formatted(this.getClass());
        log.info("MOCK {} getRefreshToken return token={}",
                this.getClass(),
                token
        );
        return token;
    }

    @Override
    public String getCookieValueSpid()
    {
        String spid = "MOCK_SPID_%s".formatted(this.getClass());
        log.info("MOCK {} getRefreshToken return spid={}",
                this.getClass(),
                spid
        );
        return spid;
    }

    @Override
    public String getCookieValueSpsc()
    {
        String spsc = "MOCK_SPSC_%s".formatted(this.getClass());
        log.info("MOCK {} getRefreshToken return spsc={}",
                this.getClass(),
                spsc
        );
        return spsc;
    }

    @Override
    public RusbondsKeys getRusbondsCred()
    {
        log.info("MOCK {} getRusbondsCred ", this.getClass());
        return new RusbondsKeys();
    }

    @Override
    public String getBondRating(int fintoolId)
    {
        exceptionProvider.exceptionErrorEvent();
        String rating = ratingProvider.getBondRating();

        log.info("MOCK {} getBondRating arg fintoolId={} return rating={}",
                this.getClass(),
                fintoolId,
                rating
        );
        return rating;
    }

    @Override
    public void makeRandomMoves()
    {
        log.info("MOCK {} makeRandomMoves ", this.getClass());
        waitSeconds(waitTime);
    }

    @Override
    public void stopProxy()
    {
        log.info("MOCK {} stopProxy ", this.getClass());
    }

    private void waitSeconds(double seconds)
    {
        synchronized (Thread.currentThread())
        {
            try
            {
                Thread.currentThread().wait((long) (1000 * seconds));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }
}
