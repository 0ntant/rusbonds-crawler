package app.service;

import app.mapper.DataCellMapper;
import app.model.Bond;
import app.model.DataCell;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class BondSnapshotService
        extends BondSheetService implements BondService
{
    DataCellService googleSheetService = new DataCellService();
    List<Bond> bondsToUpdate = new ArrayList<>();

    @Getter
    List<List<DataCell>> snapshot ;

    public BondSnapshotService()
    {
       super();
       prepareSnapshot();
    }

    public BondSnapshotService(String sheetName)
    {
        super(sheetName);
        prepareSnapshot();
    }

    public BondSnapshotService(DataCellService dataCellServ)
    {
        super(dataCellServ);
        this.googleSheetService = dataCellServ;
        prepareSnapshot();
    }


    @Override
    public List<Bond> getAll()
    {
        return DataCellMapper.mapBonds(snapshot);
    }

    @Override
    public List<Bond> getBondsToUpdate()
    {
        return DataCellMapper.mapBonds(snapshot)
                .stream()
                .filter(bond -> !bondsToUpdate.contains(bond))
                .toList();
    }

    @Override
    public Bond getBond(int row)
    {
        return DataCellMapper.mapBonds(snapshot).get(row);
    }

    @Override
    public void writeBond(Bond bond)
    {
        bondsToUpdate.add(bond);
        if(isSnapshotReadyToSave())
        {
            saveSnapshot();
        }
    }

    private boolean isSnapshotReadyToSave()
    {
        Bond lastInSnapshot = DataCellMapper.mapBond(snapshot.get(snapshot.size() - 1));
        Bond lastInBondToUpdate = bondsToUpdate.get(bondsToUpdate.size() - 1);

        return lastInSnapshot.equals(lastInBondToUpdate);
    }

    private void saveSnapshot()
    {
        log.info("Saving snapshot");
        writeBonds(bondsToUpdate);
    }

    @Override
    public void writeBonds(List<Bond> bonds)
    {
        // processCurrentSnapshot();
        mergeSnapshot(bonds);
        googleSheetService.writeDataToSheet(range, snapshot);
        rewriteBonds();
    }

    private void rewriteBonds()
    {
        log.info("Rewriting bonds");
        prepareSnapshot();
        List<Bond> bonds = DataCellMapper.mapBonds(snapshot);

        sortByYieldNowRev(bonds);
        numerateBonds(bonds);
        mergeSnapshot(bonds);

        googleSheetService.writeDataToSheet(range, snapshot);
        super.formatTable(DataCellMapper.mapBonds(snapshot));
    }

    private void prepareSnapshot()
    {
        snapshot = getCurrentSnapshot();
    }

    private void mergeSnapshot(List<Bond> bondsToWrite)
    {
        List<List<DataCell>> newSnapshot = new ArrayList<>();
        for (int i = 0; i < bondsToWrite.size(); i++)
        {
            for (List<DataCell> snapshotRow : snapshot)
            {
                List<DataCell> rowToWrite = DataCellMapper.map(bondsToWrite.get(i));
                Bond snapshotBond = DataCellMapper.mapBond(snapshotRow);
                if (snapshotBond.equals(bondsToWrite.get(i)))
                {
                    for (int j = 0; j < rowToWrite.size(); j++)
                    {
                        rowToWrite.get(j).setCellColor(snapshotRow.get(j).getCellColor());
                        rowToWrite.get(j).setNote(snapshotRow.get(j).getNote());
                        rowToWrite.get(j).setFormula(snapshot.get(i).get(j).getFormula());
                    }
                    newSnapshot.add(rowToWrite);
                    break;
                }
            }
        }

        fillSnapshotInvalidBonds(newSnapshot);
        snapshot = newSnapshot;
    }

    private void fillSnapshotInvalidBonds(List<List<DataCell>> snapshotToFill)
    {
        if (snapshot.size() > snapshotToFill.size())
        {
            for (List<DataCell> dataCells : snapshot)
            {
                Bond bondToCheck = DataCellMapper.mapBond(dataCells);
                if (bondToCheck.getIsin().isEmpty() || bondToCheck.getIsin().isBlank())
                {
                    snapshotToFill.add(dataCells);
                }
            }
        }
    }

    private void processCurrentSnapshot()
    {
        List<List<DataCell>> currentSnapshot = getCurrentSnapshot();
        String newRange = String.format(
                rangeTemplate,
                sheetName,
                rowOffset,
                getMaxRow()
        );

        if (!newRange.equals(range))
        {
            range = newRange;
            List<Bond> bondsToWrite = DataCellMapper.mapBonds(snapshot);
            for (int i = 0; i < currentSnapshot.size(); i++)
            {
                Bond bondCurrSnap = DataCellMapper.mapBond(currentSnapshot.get(i));
                if(!bondsToWrite.contains(bondCurrSnap))
                {
                    log.info("ADD bond ISIN={}", bondCurrSnap.getIsin());
                    bondsToUpdate.add(bondCurrSnap);
                }
            }
        }
        snapshot = currentSnapshot;
    }


    private List<List<DataCell>> getCurrentSnapshot()
    {
        return DataCellMapper.fillBondCells(
                googleSheetService.getTableSnapshot(range)
        );
    }
}
