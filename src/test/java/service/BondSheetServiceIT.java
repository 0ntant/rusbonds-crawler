package service;

import app.mapper.BondMapper;
import app.model.Bond;
import app.service.BondSheetService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BondSheetServiceIT
{
    BondSheetService bondSheetService = new BondSheetService();

    @Test
    void getAllBonds_checkMappingSysCreateDate_NotNull()
    {
        //given
        List<Bond> bonds = bondSheetService.getAll();
        //then
        //expected
        assertNotNull(bonds.get(0).getSysModifyDate());
    }

    @Test
    void updateBond_success()
    {
        //given
        Bond bondToUpdate = bondSheetService.getBond(1);
        //then
        bondToUpdate.setSection("New section");
        bondToUpdate.setPaperName("new Paper name");
        //expected
        bondSheetService.writeBond(bondToUpdate);
    }

    @Test
    void getBondsCount_returnMoreThanZero()
    {
        //given
        int count = bondSheetService.getMaxRow();
        //then

        //expect
        assertEquals(count, 28);
    }

    @Test
    void getAllBounds_returnListMoreThanZero()
    {
        //given
        List<Bond> bonds = bondSheetService.getAll();
        //then

        //expect
        assertFalse(bonds.isEmpty());
        assertEquals(bonds.size(), 28);
        for (Bond bond : bonds)
        {
            System.out.println(bond.getIsin() + " " + bond.getAction());
        }
    }

    @Test
    void getAllBounds_returnList28()
    {
        //given
        List<Bond> bonds = bondSheetService.getAll();
        //then

        //expect
        assertEquals(bonds.size(), 28);
    }

    @Test
    void getAllBounds_CheckCellData()
    {
        //given
        Bond bondToCheck = bondSheetService.getBond(0);
        //then
        //expect
        System.out.println(bondToCheck.getYieldNow());
        System.out.println(bondToCheck.getBodyValueIncrease());
        System.out.println(bondToCheck.getYieldPurchasePricePercent());
    }

    @Test
    void getAllBounds_CheckWriteValueFormula()
    {
        //given
        Bond bondToCheck = bondSheetService.getBond(0);
        Bond bondToCheck1 = bondSheetService.getBond(1);
        Bond bondToCheck2 = bondSheetService.getBond(2);

        bondToCheck.setSection("Tested section");
        bondToCheck1.setSection("Tested section");
        bondToCheck2.setSection("Tested section");

        //then
        bondSheetService.writeBond(bondToCheck);
        bondSheetService.writeBond(bondToCheck1);
        bondSheetService.writeBond(bondToCheck2);
        //expect
    }

    @Test
    void getBonds_checkNumericAndSort()
    {
        //given
        List<Bond> bonds = bondSheetService.getAll();
        //then
        //expect
        bondSheetService.writeBonds(bonds);
    }

    @Test
    void getBond_checkEmptyIsin()
    {
        //given
        Bond bondToCheck = bondSheetService.getBond(0);
        //then
        //expect
        assertEquals(bondToCheck.getIsin(), "");
    }

    @Test
    void checkEmptyIsinBonds_Equals()
    {
        //given
        Bond bondToCheck = bondSheetService.getBond(0);
        Bond bondToCheck1 = bondSheetService.getBond(1);
        Bond bondToCheckCopy =  bondSheetService.getBond(0);
        Bond bondToCheckCopy1 =  bondSheetService.getBond(1);

        //then

        bondToCheck.setRating("new raitind");
        bondToCheck1.setRating("new rating1");

        //expected
        assertEquals(bondToCheck.getIsin(), "");
        assertEquals(bondToCheck1.getIsin(), "");
        assertNotEquals(bondToCheck, bondToCheck1);
        assertEquals(bondToCheck, bondToCheckCopy);
        assertEquals(bondToCheck1, bondToCheckCopy1);
    }

    @Test
    void setEmptyBondIsin()
    {
        //given
        Bond bondToCheck = bondSheetService.getBond(2);

        //then

        bondToCheck.setIsin("");
        bondSheetService.writeBond(bondToCheck);
        //expected
    }

    @Test
    void getPureRatingBond()
    {
        //given
        List<Bond> bonds = bondSheetService.getAll();
        //then

        //expected
        for (Bond bond : bonds)
        {
            System.out.println(bond.getIsin() + " " + BondMapper.pureRating(bond.getRating()));
        }
    }

}
