package app.service;

public interface SelRusbondsServiceProxy extends SelRusbondsService
{
    void createAuthSession();

    String getBondRating(int fintoolId);

    void stopProxy();
}
