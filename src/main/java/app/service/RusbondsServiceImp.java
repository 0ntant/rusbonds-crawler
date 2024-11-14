package app.service;

import app.exception.InvalidIsinException;
import app.integration.rusbonds.RusbondsClient;
import app.mapper.RusbondMapper;
import app.model.RusbondsKeys;
import app.model.Bond;
import com.fasterxml.jackson.databind.JsonNode;

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
