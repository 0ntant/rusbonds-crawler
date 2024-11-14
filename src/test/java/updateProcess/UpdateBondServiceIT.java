package updateProcess;

import app.service.AccountingPolicySheetService;
import app.service.BondSnapshotService;
import app.service.DataCellService;
import app.service.UpdateBondService;
import mock.RusbondsServiceMock;
import mock.SelRusbondsServiceProxyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(MockitoExtension.class)
public class UpdateBondServiceIT
{
    DataCellService dataCellService = new DataCellService();

    @Spy
    UpdateBondService updateBondService = UpdateBondService.builder()
            .accServ(new AccountingPolicySheetService(dataCellService))
            .bondServ(new BondSnapshotService("TestData"))
            .rusbondsServ(new RusbondsServiceMock(0.01))
            .selRusbondsSer(new SelRusbondsServiceProxyMock(0.01))
            .build();

    @Test
    void startUpdate()
    {
        //given
        doNothing().when(updateBondService).waitMinutes(anyInt());

        //then
        updateBondService.startUpdateProcess();
        //expected
    }
}
