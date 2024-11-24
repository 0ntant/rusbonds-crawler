package updateProcess;

import app.model.Bond;
import app.service.AccountingPolicySheetService;
import app.service.BondSnapshotService;
import app.service.DataCellService;
import app.service.UpdateBondService;
import mock.RusbondsServiceMock;
import mock.SelRusbondsServiceProxyMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
