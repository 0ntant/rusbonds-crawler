package updateProcess;

import app.model.Bond;
import app.model.Env;
import app.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
public class UpdateBondServiceIT
{
    DataCellService dataCellService = new DataCellService();

    @Spy
    UpdateBondService updateBondService = UpdateBondService.builder()
            .accServ(new AccountingPolicySheetService(dataCellService))
            .bondServ(new BondSnapshotService("TestData"))
            .rusbondsServ(new RusbondsServiceImp())
            .selRusbondsSer(new SelRusbondsServiceProxyImp())
            .notificationService(new NotificationService(Env.TEST))
            .bondValidatorService(new BondValidatorService())
            .build();

    @Test
    void startUpdate()
    {
        //given
        doReturn(true).when(updateBondService).isNeedUpdate();
        //then
        updateBondService.startUpdateProcess();
        //expected
    }
}