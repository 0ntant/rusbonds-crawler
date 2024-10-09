package service;

import app.model.Bond;
import app.service.BondSheetService;
import app.service.UpdateSheetService;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UpdateSheetServiceIT
{
    UpdateSheetService updateSheetSrv = new UpdateSheetService();
    BondSheetService bondSheetServ= new BondSheetService();

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
}
