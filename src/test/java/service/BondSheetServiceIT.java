package service;

import app.mapper.BondMapper;
import app.model.Bond;
import app.service.BondService;
import app.service.BondSheetService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class BondSheetServiceIT
{
    BondService bondService = new BondSheetService("TestData");


//    @Test
//    void getDataCells()
//    {
//        //given
//        List<List<DataCell>> dataCells = bondSheetService.getDataCells();
//        //then
//
//        //expected
//        for(int i = 0; i < 5 ; i++)
//        {
//            List<DataCell> dataCells1 = dataCells.get(i);
//            for (int j = 0; j < 10 ; j++)
//            {
//                System.out.printf("%s %s %s %s",
//                        dataCells1.get(j).getValue(),
//                        dataCells1.get(j).getNote(),
//                        dataCells1.get(j).getCellColor(),
//                        dataCells1.get(j).getFormula()
//                );
//                System.out.println();
//            }
//        }
//    }

    @Test
    void rewrite_Bond()
    {
        //given
        Bond bond = bondService.getBond(6);
        //then
        //expected
        bondService.writeBond(bond);
    }

    @Test
    void getLastBond_fromAllBond()
    {
        //given
        List<Bond> bonds = bondService.getAll();

        //then
        //expected
        assertEquals("RU000A104JV3", bonds.get(bonds.size()-1).getIsin());
    }

    @Test
    void rewrite_setCompTicketLiveTimeYear()
    {
        //given
        Bond bond = bondService.getBond(0);
        System.out.println(bond.getTicketLiveTimeYear());
        //then
        bond.setCompTicketLiveTimeYear();
        //expected
        System.out.println(bond.getTicketLiveTimeYear());
        bondService.writeBond(bond);
    }

    @Test
    void mapping_withBrokerValues_withNewPositionAction_sysModDate()
    {
        //given
        Bond bond = bondService.getBond(0);
        //then
        //expected
        assertEquals( 1,bond.getAction());
        assertEquals( "Брокер1",bond.getBroker());

        assertEquals( 1,bond.getSysModifyDate().getDayOfMonth());
        assertEquals( Month.JANUARY,bond.getSysModifyDate().getMonth());
        assertEquals( 2023,bond.getSysModifyDate().getYear());
    }

    @Test
    void mapping_withVoid()
    {
        //given
        Bond bond = bondService.getBond(1);
        //then
        //expected
        assertEquals( 0,bond.getNumber());
        assertEquals( 0,bond.getMarketValueNow());
        assertEquals("", bond.getRating());
        assertEquals( 0,bond.getAction());
        assertEquals( "",bond.getBroker());
        assertEquals(LocalDate.MIN, bond.getSysModifyDate());
    }

    @Test
    void mapping_withBigNumericValues()
    {
        //given
        Bond bond = bondService.getBond(2);
        //then
        //expected
    }


    @Test
    void getAllBonds_checkMappingSysCreateDate_NotNull()
    {
        //given
        List<Bond> bonds = bondService.getAll();
        //then
        //expected
        assertNotNull(bonds.get(0).getSysModifyDate());
    }

    @Test
    void updateBond_success()
    {
        //given
        Bond bondToUpdate = bondService.getBond(1);
        //then
        bondToUpdate.setSection("New section");
        bondToUpdate.setPaperName("new Paper name");
        //expected
        bondService.writeBond(bondToUpdate);
    }

    @Test
    void getBondsCount_return29()
    {
        //given
        int count = bondService.getMaxRow();
        //then

        //expect
        assertEquals(count, 29);
    }

    @Test
    void getAllBounds_returnListMoreThanZero()
    {
        //given
        List<Bond> bonds = bondService.getAll();
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
        List<Bond> bonds = bondService.getAll();
        //then

        //expect
        assertEquals(bonds.size(), 28);
    }

    @Test
    void getAllBounds_CheckCellData()
    {
        //given
        Bond bondToCheck = bondService.getBond(0);
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
        Bond bondToCheck = bondService.getBond(0);
        Bond bondToCheck1 = bondService.getBond(1);
        Bond bondToCheck2 = bondService.getBond(2);

        bondToCheck.setSection("Tested section");
        bondToCheck1.setSection("Tested section");
        bondToCheck2.setSection("Tested section");

        //then
        bondService.writeBond(bondToCheck);
        bondService.writeBond(bondToCheck1);
        bondService.writeBond(bondToCheck2);
        //expect
    }

    @Test
    void getBonds_checkNumericAndSort()
    {
        //given
        List<Bond> bonds = bondService.getAll();
        //then
        //expect
        bondService.writeBonds(bonds);
    }

    @Test
    void getBond_checkEmptyIsin()
    {
        //given
        Bond bondToCheck = bondService.getBond(0);
        //then
        //expect
        assertEquals(bondToCheck.getIsin(), "");
    }

    @Test
    void checkEmptyIsinBonds_Equals()
    {
        //given
        Bond bondToCheck = bondService.getBond(0);
        Bond bondToCheck1 = bondService.getBond(1);
        Bond bondToCheckCopy =  bondService.getBond(0);
        Bond bondToCheckCopy1 =  bondService.getBond(1);

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
        Bond bondToCheck = bondService.getBond(2);

        //then

        bondToCheck.setIsin("");
        bondService.writeBond(bondToCheck);
        //expected
    }

    @Test
    void getPureRatingBond()
    {
        //given
        List<Bond> bonds = bondService.getAll();
        //then

        //expected
        for (Bond bond : bonds)
        {
            System.out.println(bond.getIsin() + " " + BondMapper.pureRating(bond.getRating()));
        }
    }

}
