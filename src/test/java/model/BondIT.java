package model;

import app.model.Bond;
import app.service.BondService;
import app.service.BondSheetService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BondIT
{
    BondService bondService = new BondSheetService();

    @Test
    void bondModifyField_checkEquals()
    {
        //given
        Bond bond1 = bondService.getAll().get(0);
        Bond bond2 = bondService.getAll().get(0);

        //then
        bond2.setMarketValueNow(111.34);
        bond2.setRating("NEW rating");

        //expected
        assertNotEquals(bond1.getMarketValueNow(), bond2.getMarketValueNow());
        assertNotEquals(bond1.getRating(), bond2.getRating());
        assertEquals(bond1, bond2);
    }

    //        GoogleSheetService googleSheetService = new GoogleSheetService("Sheet1");
//        Bond bond = Bond.builder()
//                .number(1)
//                .issuerName("Завод алюминиевых систем")
//                .paperName("ЗАС БО-ПО1")
//                .isin("RU0001")
//                .section("Машиностроение")
//                .rate(13.1)
//                .yieldPurchasePricePercent(124.4)
//                .marketValuePurchaseTime(1.44)
//                .yieldNow(765.3)
//                .marketValueNow(123.4)
//                .bodyValueIncrease(13.4)
//                .ticketExpirationDate(LocalDate.now())
//                .ticketLiveTimeYear(79.133)
//                .paperRepaymentTime(LocalDate.now())
//                .paymentRatePerYear(1)
//                .count(13)
//                .rating("SSS")
//                .activity("activity")
//                .action("")
//                .build();
//        googleSheetService.writeEmptyRow(BondMapper.map(bond));
//        BondSheetService bondService = new BondSheetService();
//        List<Bond> bondList = bondService.getAll();
//        System.out.println(bond.getIssuerName());
//        bondService.writeBondToRow(bond, 37);
//        bondService.writeBondToRow(bond, 38);
//          bondService.writeBonds(bondList);
}
