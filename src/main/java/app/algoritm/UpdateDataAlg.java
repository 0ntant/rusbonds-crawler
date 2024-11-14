package app.algoritm;

import app.service.*;

public class UpdateDataAlg
{
    public static void start()
    {
        DataCellService dataCellService = new DataCellService();
        UpdateBondService updateBondService = UpdateBondService.builder()
                .accServ(new AccountingPolicySheetService(dataCellService))
                .bondServ(new BondSnapshotService(dataCellService))
                .rusbondsServ(new RusbondsServiceImp())
                .selRusbondsSer(new SelRusbondsServiceProxyImp())
                .build();

        updateBondService.startUpdateProcess();
    }
}
