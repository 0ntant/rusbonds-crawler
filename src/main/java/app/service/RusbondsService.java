package app.service;

import app.model.Bond;
import app.model.RusbondsKeys;

public interface RusbondsService {
    void setRusbondsKeys(RusbondsKeys rusbondsKeys);

    int getIssuerId(int findtoolId);

    int getFintoolId(Bond bond);

    double getMarketValueNow(int findtoolId);

    String getBondRating(int issuerId);

    void login();
}
