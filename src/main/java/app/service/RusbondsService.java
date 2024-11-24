package app.service;

import app.model.Bond;
import app.model.BondRepayment;
import app.model.RusbondsKeys;

import java.util.List;

public interface RusbondsService
{
    List<BondRepayment> getRepayments(int findtoolId);

    void setRusbondsKeys(RusbondsKeys rusbondsKeys);

    int getIssuerId(int findtoolId);

    int getFintoolId(Bond bond);

    double getMarketValueNow(int findtoolId);

    String getBondRating(int issuerId);

    void login();
}
