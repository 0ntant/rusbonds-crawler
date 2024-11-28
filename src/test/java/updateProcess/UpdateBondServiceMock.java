package updateProcess;

import app.model.Env;
import app.service.*;
import mock.RusbondsServiceMock;
import mock.SelRusbondsServiceProxyMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UpdateBondServiceMock
{
    DataCellService dataCellService = new DataCellService();

    @Spy
    UpdateBondService updateBondService = UpdateBondService.builder()
            .accServ(new AccountingPolicySheetService(dataCellService))
            .bondServ(new BondSnapshotService(dataCellService,"TestData"))
            .rusbondsServ(new RusbondsServiceMock(0.01))
            .selRusbondsSer(new SelRusbondsServiceProxyMock(0.01))
            .notificationService(new NotificationService(Env.TEST))
            .bondValidatorService(new BondValidatorService())
            .build();

    @Test
    void startUpdate()
    {
        //given
        doNothing().when(updateBondService).waitMinutes(anyInt());
        doReturn(true).when(updateBondService).isNeedUpdate();

        //then
        updateBondService.startUpdateProcess();
        //expected
    }
}
