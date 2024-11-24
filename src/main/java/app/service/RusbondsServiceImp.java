package app.service;

import app.exception.InvalidIsinException;
import app.integration.rusbonds.RusbondsClient;
import app.mapper.RusbondMapper;
import app.model.BondRepayment;
import app.model.RusbondsKeys;
import app.model.Bond;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RusbondsServiceImp implements RusbondsService
{
    RusbondsClient client;

    public RusbondsServiceImp()
    {
        this.client = new RusbondsClient();
    }

    public RusbondsServiceImp(RusbondsKeys rusbondsKeys)
    {
        client = new RusbondsClient(rusbondsKeys);
    }

    @Override
    public List<BondRepayment> getRepayments(int findtoolId)
    {
        JsonNode json = client.getCalendarList(
                findtoolId,
                "MTY");

        if(json.isEmpty() || json.isNull())
        {
            log.warn("findtoolId={}, REPAYMENTS_IS_EMPTY"
                    ,findtoolId
            );
            return List.of();
        }

        List<BondRepayment>  repayments = RusbondMapper.repayments(json);
        log.info("findtoolId={},repayments={}"
                ,findtoolId
                ,repayments
        );
        return repayments;
    }

    @Override
    public void setRusbondsKeys(RusbondsKeys rusbondsKeys)
    {
        client.setRusbondsKeys(rusbondsKeys);
    }

    @Override
    public int getIssuerId(int findtoolId)
    {
        return RusbondMapper.issuerId(client.getIssuerId(findtoolId));
    }

    @Override
    public int getFintoolId(Bond bond)
    {
        String isin = bond.getIsin();
        JsonNode json = client.getFindtoolId(isin);

        if (isFintoolJsonInvalid(isin, json))
        {
            throw new InvalidIsinException(
                    String.format(
                            "ISIN=%s not found",
                            isin
                    )
            );
        }
        return RusbondMapper.fintoolId(json);
    }

    private boolean isFintoolJsonInvalid(String isin,JsonNode json)
    {
        if (json.path("entities").isEmpty())
        {
            return true;
        }

        if (!RusbondMapper.isin(json).equals(isin))
        {
            return true;
        }
        return false;
    }

    @Override
    public double getMarketValueNow(int findtoolId)
    {
        return RusbondMapper.marketValueNow(
                client.getMarketValueNow(findtoolId)
        );
    }

    @Override
    public String getBondRating(int issuerId)
    {
        return RusbondMapper.ratingRu(
                client.getRating(issuerId)
        );
    }

    @Override
    public void login()
    {
        client.login();
    }
}
