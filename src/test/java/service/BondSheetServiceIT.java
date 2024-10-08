package service;

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
    void getBondsCount_return28()
    {
        //given
        int count = bondSheetService.getMaxRow();
        //then

        //expect
        assertEquals(count, 28);
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

}
