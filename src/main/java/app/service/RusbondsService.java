package app.service;

import app.integration.rusbonds.RusbondsClient;
import app.model.RusbondsKeys;
import app.model.Bond;

public class RusbondsService
{
    RusbondsClient client;

    public RusbondsService()
    {
        this.client = new RusbondsClient();
    }

    public RusbondsService(RusbondsKeys rusbondsKeys)
    {
        client = new RusbondsClient(rusbondsKeys);
    }

    public void setRusbondsKeys(RusbondsKeys rusbondsKeys)
    {
        client.setRusbondsKeys(rusbondsKeys);
    }

    public int getIssuerId(int findtoolId)
    {
        return client.getIssuerId(findtoolId);
    }

    public int getFintoolId(Bond bond)
    {
        return client.getFindtoolId(bond.getIsin());
    }


    public double getMarketValueNow(int findtoolId)
    {
        return client.getMarketValueNow(findtoolId);
    }

    public String getBondRating(int issuerId)
    {
        return client.getRating(issuerId);
    }

    public void login()
    {
        client.login();
    }
}
