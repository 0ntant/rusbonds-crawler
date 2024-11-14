package service;

import app.mapper.BondMapper;
import app.model.Bond;
import app.service.BondService;
import app.service.BondSheetService;
import app.service.UpdateBondService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class UpdateBondServiceIT
{
    UpdateBondService updateSheetSrv = new UpdateBondService();
    BondService bondSheetServ = new BondSheetService();

    @Test
    void bondsToUpdate_showList()
    {
        //given
        List<Bond> bondsToUpdate = updateSheetSrv.getBondsToUpdate();
        //then

        //expected

        for(Bond bond : bondsToUpdate)
        {
            System.out.println(bond.getIssuerName() + " " + bond.getIsin());
        }
    }

    @Test
    void saveEmptyIsin_saveEmptyIsin()
    {
        //given
        Bond bond = bondSheetServ.getBond(0);
        //then
        bond.setIsin("");
        updateSheetSrv.saveUpdateBond(bond);
        //expected
    }

    @Test
    void multipleRating()
    {
        //given
        String multiRating = "ruB+/BB-.ru";
        String usualRating = "BB(RU)";
        //then

        //expected
        assertEquals(2, multiRating.split("/").length);
        assertEquals("B+", BondMapper.pureRating(multiRating.split("/")[0]));
        assertEquals("BB-",  BondMapper.pureRating(multiRating.split("/")[1]));


        assertEquals(1, usualRating.split("/").length);
        assertEquals(usualRating, usualRating.split("/")[0]);
    }


    @Test
    void getLowestRating_BB()
    {
        //given
        List<String> ratings = List.of("A+", "BB", "AA", "BBB-");

        //then
        String lowestRating = updateSheetSrv.getLowestRating(ratings);

        //expected
        assertEquals("BB", lowestRating);
    }

    @Test
    void getLotCount_byRatingTitle()
    {
        //given
        String rating = "A+";

        //then
        int lotCount = updateSheetSrv.getAccRatingLotCount(rating);
        //expected
        assertEquals(40, lotCount);
    }
}
