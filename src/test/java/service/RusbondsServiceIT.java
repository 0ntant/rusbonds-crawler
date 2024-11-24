package service;

import app.exception.InvalidIsinException;
import app.model.Bond;
import app.model.BondRepayment;
import app.service.RusbondsService;
import app.service.RusbondsServiceImp;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RusbondsServiceIT
{
    RusbondsService rusbondsServ = new RusbondsServiceImp();

    @Test
    void getRepayments_success()
    {
        rusbondsServ.login();
        //given
        List<BondRepayment> repayments = rusbondsServ.getRepayments(237504);

        //then
        //expected
       System.out.printf(repayments.toString());
    }

    @Test
    void getFintoolId_InvalidIsinException()
    {
        rusbondsServ.login();
        //given
        String invalidIsin = "INVALID_ISIN";
        Bond bond = new Bond();
        bond.setIsin(invalidIsin);
        String invalidIsinMessage = String.format( "ISIN=%s not found",invalidIsin);
        //then
        //expected
        InvalidIsinException exception = assertThrows(InvalidIsinException.class, () ->
                rusbondsServ.getFintoolId(bond));
        assertEquals(exception.getMessage(),invalidIsinMessage);
    }

    @Test
    void getFintoolId_ValidISIN()
    {
        rusbondsServ.login();

        //given
        String validIsin = "RU000A107G22";
        Bond bond = new Bond();
        bond.setIsin(validIsin);

        //then
        int fintoolId = rusbondsServ.getFintoolId(bond);

        //expected
        assertEquals(236999, fintoolId);
    }
}
