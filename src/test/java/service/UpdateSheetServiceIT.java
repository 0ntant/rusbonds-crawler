package service;

import app.model.Bond;
import app.service.UpdateSheetService;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UpdateSheetServiceIT
{
    UpdateSheetService updateSheetSrv = new UpdateSheetService();

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
}
