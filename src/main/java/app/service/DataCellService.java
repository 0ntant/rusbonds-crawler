package app.service;

import app.model.DataCell;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static app.config.GoogleConfig.SPREADSHEET_ID;

@Slf4j
public class DataCellService extends GoogleSheetService
{
    public DataCellService()
    {
        super();
    }

    public  List<List<DataCell>> getTableSnapshot(String range)
    {
        try
        {
            List<List<DataCell>> dataCells = new ArrayList<>();

            Spreadsheet sheet = sheetsService.spreadsheets().get(SPREADSHEET_ID)
                    .setRanges(List.of(range))
                    .setFields("sheets.data.rowData.values(userEnteredValue,effectiveValue,userEnteredFormat.backgroundColor,note)")
                    .execute();

            for (Sheet sheetData : sheet.getSheets())
            {
                for (GridData gridData : sheetData.getData())
                {
                    if (gridData.getRowData() == null) continue;
                    for (RowData rowData : gridData.getRowData())
                    {
                        List<DataCell> rowCells = new ArrayList<>();
                        if (rowData.getValues() == null) continue;
                        for (CellData cellData : rowData.getValues())
                        {
                            Object value = null;
                            String formula = null;
                            if (cellData.getUserEnteredValue() != null)
                            {
                                if (cellData.getEffectiveValue().getStringValue() != null)
                                {
                                    value = cellData.getEffectiveValue().getStringValue();
                                }
                                else if (cellData.getEffectiveValue().getNumberValue() != null)
                                {
                                    value = cellData.getEffectiveValue().getNumberValue();
                                }
                                else if (cellData.getEffectiveValue().getBoolValue() != null)
                                {
                                    value = cellData.getEffectiveValue().getBoolValue();
                                }
                            }

                            if (cellData.getUserEnteredValue() != null
                                    && cellData.getUserEnteredValue().getFormulaValue() != null) {
                                formula = cellData.getUserEnteredValue().getFormulaValue();
                            }

                            String note = cellData.getNote();
                            Color color = cellData.getUserEnteredFormat() != null ? cellData.getUserEnteredFormat().getBackgroundColor() : null;

                            rowCells.add(DataCell.builder()
                                    .value(value)
                                    .note(note)
                                    .cellColor(color)
                                    .formula(formula)
                                    .build());
                        }
                        dataCells.add(rowCells);
                    }
                }
            }

            return dataCells;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void writeDataToSheet(String range, List<List<DataCell>> data)
    {
        List<Request> requests = new ArrayList<>();
        List<List<Object>> values = new ArrayList<>();
        int sheetId =  getSheetIdByRange(range);

        String cellRange = range.split("!")[1];
        String startCell = cellRange.split(":")[0];

        try
        {
            int rowIndex = Integer.parseInt(startCell.replaceAll("[^0-9]", "")) - 1;
            for (List<DataCell> rowData : data)
            {
                List<Object> rowValues = new ArrayList<>();
                int colIndex = columnLetterToIndex(startCell.replaceAll("[0-9]", ""));
                for (DataCell cell : rowData)
                {
                    if (cell.getFormula() != null)
                    {
                        rowValues.add(cell.getFormula());
                    }
                    else
                    {
                        rowValues.add(cell.getValue());
                    }


                    GridRange gridRange = new GridRange()
                            .setSheetId(sheetId)
                            .setStartRowIndex(rowIndex)
                            .setEndRowIndex(rowIndex + 1)
                            .setStartColumnIndex(colIndex)
                            .setEndColumnIndex(colIndex + 1);

                    if (cell.getNote() != null)
                    {
                        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                                .setRange(gridRange)
                                .setCell(new CellData().setNote(cell.getNote()))
                                .setFields("note")));
                    }
                    else
                    {
                        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                                .setRange(gridRange)
                                .setCell(new CellData().setNote(null))
                                .setFields("note")));
                    }

                    if (cell.getCellColor() != null)
                    {
                        Color cellColor = cell.getCellColor();
                        CellFormat cellFormat = new CellFormat().setBackgroundColor(cellColor);
                        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                                .setRange(gridRange)
                                .setCell(new CellData().setUserEnteredFormat(cellFormat))
                                .setFields("userEnteredFormat.backgroundColor")));
                    }
                    else
                    {
                        CellFormat cellFormat = new CellFormat().setBackgroundColor(null);
                        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                                .setRange(gridRange)
                                .setCell(new CellData().setUserEnteredFormat(cellFormat))
                                .setFields("userEnteredFormat.backgroundColor")));
                    }
                    colIndex++;
                }
                values.add(rowValues);
                rowIndex++;
            }

            ValueRange body = new ValueRange().setValues(values);
            sheetsService.spreadsheets().values().update(SPREADSHEET_ID, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            if (!requests.isEmpty())
            {
                BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                sheetsService.spreadsheets()
                        .batchUpdate(SPREADSHEET_ID, batchUpdateRequest)
                        .execute();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public Integer getSheetIdByRange(String range)
    {
        try
        {
            String sheetName = range.contains("!") ? range.split("!")[0] : range;
            Spreadsheet spreadsheet = sheetsService.spreadsheets()
                    .get(SPREADSHEET_ID)
                    .setFields("sheets.properties")
                    .execute();
            List<Sheet> sheets = spreadsheet.getSheets();
            for (Sheet sheet : sheets)
            {
                SheetProperties properties = sheet.getProperties();
                if (sheetName.equals(properties.getTitle()))
                {
                    return properties.getSheetId();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
        throw new NoSuchElementException("No range %s".formatted(range));
    }
}

