package app.service;

import app.mapper.BondMapper;
import app.mapper.RusbondMapper;
import app.model.Bond;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BondSheetService
{
    final String sheetName = "Sheet1";
    final int rowOffset = 2;
    final String rangeTemplate = "%s!A%s:T%s";

    String range;
    GoogleSheetService googleSheetService ;

    public BondSheetService()
    {
        this.googleSheetService = new GoogleSheetService();
        int maxRow = getMaxRow();
        this.range = String.format(rangeTemplate, sheetName, rowOffset,maxRow);
    }

    public List<Bond> getAll()
    {
        List<Bond> bonds = new ArrayList<>();
        List<List<Object>> objects = googleSheetService.getAllTable(range);
        for(List<Object> objects1 : objects)
        {
            bonds.add(BondMapper.map(objects1));
        }
        return bonds;
    }

    public Bond getBond(int row)
    {
        List<Object> objects = googleSheetService.getRow(range, row);
        return BondMapper.map(objects);
    }

    public int getMaxRow()
    {
        return googleSheetService.findMaxRows(String.format("%s!D:D",sheetName)) - rowOffset + 1;
    }

    public void writeBond(Bond bond)
    {
        List<Bond> bonds = getAll();
        int row = bonds.indexOf(bond) + rowOffset;
        String range = String.format(rangeTemplate, sheetName, row , row);
        googleSheetService.writeRow(range, BondMapper.map(bond));
        formatTable(List.of(bond));
    }

    public void writeBonds(List<Bond> bonds)
    {
        String rangeToWrite = String.format(
                rangeTemplate,
                sheetName,
                rowOffset,
                bonds.size() + 1
        );

        List<List<Object>> bondObjects = bonds
                .stream()
                .map(BondMapper::map)
                .toList();
        googleSheetService.writeAllTable(rangeToWrite,bondObjects);
        formatTable(bonds);
    }

    private void  formatTable(List<Bond> bonds)
    {
        String rangeToFormatNow =  String.format("%s!H%s:H%s",sheetName, rowOffset,bonds.size()+1);
        String rangeToFormatPurchase =  String.format("%s!J%s:J%s",sheetName, rowOffset,bonds.size()+1);

        googleSheetService.setColumnFormatCurrency(rangeToFormatNow);
        googleSheetService.setColumnFormatCurrency(rangeToFormatPurchase);
    }

    public void writeModifyDate()
    {
        googleSheetService.writeCell(
                getModifyDateRange(),
                BondMapper.modDateMap(LocalDate.now())
        );
    }

    public LocalDate getModifyDate()
    {
        return BondMapper.modDateMap(
                googleSheetService.getCell(
                        getModifyDateRange()
                )
        );
    }

    public String exportData()
    {
        File file = new File("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d-yyyy");
        LocalDate localDate = LocalDate.now();
        String dumpDir = String.format(
                "/%s/dump/%s.csv",
                file.getAbsoluteFile(),
                localDate.format(formatter));
        googleSheetService.exportTable(range, dumpDir);
        return dumpDir;
    }

    public void importData(String dumpFile)
    {
        googleSheetService.exportTable(range, dumpFile);
    }

    private String getModifyDateRange()
    {
        int maxRow = getMaxRow() + 5;
        return String.format("%s!B%s",sheetName, maxRow);
    }
}
