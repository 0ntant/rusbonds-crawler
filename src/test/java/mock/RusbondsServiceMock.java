package mock;

import app.model.Bond;
import app.model.RusbondsKeys;
import app.service.RusbondsService;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class RusbondsServiceMock implements RusbondsService
{
    Random random = new Random();
    double waitTime;
    RatingProvider ratingProvider = new RatingProvider();
    ExceptionProvider exceptionProvider = new ExceptionProvider();

    public RusbondsServiceMock(double waitTime)
    {
        this.waitTime = waitTime;
    }

    @Override
    public void setRusbondsKeys(RusbondsKeys rusbondsKeys)
    {
        log.info("MOCK {} createAuthSession", this.getClass());
    }

    @Override
    public int getIssuerId(int findtoolId)
    {
        int issuerId = random.nextInt(10_000, 99_999);
        log.info("MOCK {} getIssuerId arg findtoolId={} return issuerId={}",
                this.getClass(),
                findtoolId,
                issuerId
        );
        waitSeconds(waitTime);
        return issuerId;
    }

    @Override
    public int getFintoolId(Bond bond)
    {
        int fintoolId = random.nextInt(10_000, 99_999);
        log.info("MOCK {} getFintoolId arg bond={} return fintoolId={}",
                this.getClass(),
                bond,
                fintoolId
        );
        waitSeconds(waitTime);
        return fintoolId;
    }

    @Override
    public double getMarketValueNow(int findtoolId)
    {
        int marketValueNow = random.nextInt(10, 100);
        log.info("MOCK {} getMarketValueNow arg findtoolId={} return marketValueNow={}",
                this.getClass(),
                findtoolId,
                marketValueNow
        );
        waitSeconds(waitTime);
        return marketValueNow;
    }

    @Override
    public String getBondRating(int issuerId)
    {
        exceptionProvider.exceptionErrorEvent();
        String rating = ratingProvider.getBondRating();

        log.info("MOCK {} getBondRating arg fintoolId={} return rating={}",
                this.getClass(),
                issuerId,
                rating
        );
        waitSeconds(waitTime);
        return rating;
    }

    @Override
    public void login()
    {
        log.info("MOCK {} login", this.getClass());
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
