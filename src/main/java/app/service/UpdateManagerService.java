package app.service;

import app.model.GoogleSheet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Slf4j
public class UpdateManagerService {
    List<GoogleSheet> googleSheets;

    public UpdateManagerService()
    {
        googleSheets = new ArrayList<>();

        googleSheets.add(new GoogleSheet("Sheet1"));
        googleSheets.add(new GoogleSheet("Sheet3"));
    }

    public void updateOldestSheet()
    {
        GoogleSheet oldestSheet = getOldest();
        log.info("Oldest sheet to update: {} update date: {}",
                oldestSheet.getName(),
                oldestSheet.getModifyDate()
        );

        DataCellService dataCellService = new DataCellService();
        UpdateBondService updateBondService = UpdateBondService.builder()
                .accServ(new AccountingPolicySheetService(dataCellService))
                .bondServ(new BondSnapshotService(dataCellService, oldestSheet.getName()))
                .rusbondsServ(new RusbondsServiceImp())
                .selRusbondsSer(new SelRusbondsServiceProxyImp())
                .build();

        updateBondService.startUpdateProcess();
    }

    public GoogleSheet getOldest()
    {
        setModifyDate();
        return googleSheets.stream()
                .min(Comparator.comparing(GoogleSheet::getModifyDate))
                .orElseThrow(() ->
                        new  NoSuchElementException(
                                "No oldest google sheets to update"
                        )
                );
    }

    private void setModifyDate()
    {
        for(GoogleSheet googleSheet : googleSheets)
        {
            BondService bondService = new BondSheetService(googleSheet.getName());
            googleSheet.setModifyDate(bondService.getModifyDate());
        }
    }
}
