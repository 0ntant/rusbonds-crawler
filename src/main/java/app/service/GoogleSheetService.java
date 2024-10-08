package app.service;

import static app.config.GoogleConfig.*;
import app.util.GoogleUtil;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GoogleSheetService
{
    Sheets sheetsService;

    public GoogleSheetService()
    {
        try
        {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            this.sheetsService =  new Sheets.Builder(
                   GoogleNetHttpTransport.newTrustedTransport(),
                   GsonFactory.getDefaultInstance(), GoogleUtil.auth(HTTP_TRANSPORT))
                   .setApplicationName(APPLICATION_NAME)
                   .build();
        } catch (Exception ex)
       {
           ex.printStackTrace();
           log.error(ex.getMessage());
           throw new RuntimeException(ex);}
    }

    public void writeCell(String range, Object data)
    {
        writeRow(range, List.of(data));
    }

    public void writeRow(String range, List<Object> data)
    {
       writeAllTable(range, List.of(data));
    }

    public void writeAllTable(String range, List<List<Object>> data)
    {
        ValueRange bodyToWrite = new ValueRange().setValues(data);
        try
        {
            sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, range, bodyToWrite)  // Указываем ID таблицы, диапазон и данные
                    .setValueInputOption("RAW")  // RAW - записываем данные как есть, без форматирования
                    .execute();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public Object getCell(String range)
    {
        return getRow(range,0).get(0);
    }

    public List<Object> getRow(String range, int row)
    {
        return getAllTable(range).get(row);
    }

    public List<List<Object>> getAllTable(String range)
    {
        try
        {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            return response.getValues();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public int findMaxRows(String range)
    {
        try
        {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            return (values == null || values.isEmpty()) ? 1 : values.size() + 1;
        }catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void setColumnFormatCurrency(String range)
    {
        String sheetName = range.split("!")[0];
        String cellRange = range.split("!")[1];

        String startCell = cellRange.split(":")[0];
        String endCell = cellRange.split(":")[1];

        int startColumnIndex = columnLetterToIndex(startCell.replaceAll("[0-9]", ""));
        int endColumnIndex = columnLetterToIndex(endCell.replaceAll("[0-9]", "")) + 1;
        int startRowIndex = Integer.parseInt(startCell.replaceAll("[^0-9]", "")) - 1;
        int endRowIndex = Integer.parseInt(endCell.replaceAll("[^0-9]", ""));

        try
        {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
        Integer sheetId = spreadsheet.getSheets().stream()
                .filter(sheet -> sheet.getProperties().getTitle().equals(sheetName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sheet not found"))
                .getProperties().getSheetId();

        CellFormat cellFormat = new CellFormat()
                .setNumberFormat(new NumberFormat()
                                .setType("CURRENCY")
                        .setPattern("\"p.\"#,##0.00"));
        Request formatRequest = new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(sheetId)
                                .setStartRowIndex(startRowIndex)
                                .setEndRowIndex(endRowIndex)
                                .setStartColumnIndex(startColumnIndex)
                                .setEndColumnIndex(endColumnIndex))
                        .setCell(new CellData().setUserEnteredFormat(cellFormat))
                        .setFields("userEnteredFormat.numberFormat"));
                        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Arrays.asList(formatRequest));
            sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, batchUpdateRequest).execute();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void exportTable(String range, String fileName)
    {
        String sheetName = range.split("!")[0];
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, sheetName + "!A1:Z")
                    .execute();
            List<List<Object>> values = response.getValues();
            for (List<Object> row : values)
            {
                writer.write(String.join(",", row.stream().map(Object::toString).toArray(String[]::new)));
                writer.newLine();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void importCsvToSheet(String range, String csvFilePath)
    {
        List<List<Object>> values = new ArrayList<>();
        String sheetName = range.split("!")[0];
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] row = line.split(",");
                List<Object> rowData = new ArrayList<>(Arrays.asList(row));
                values.add(rowData);
                ValueRange body = new ValueRange().setValues(values);
                sheetsService.spreadsheets().values()
                        .update(SPREADSHEET_ID, sheetName+"!A1", body)
                        .setValueInputOption("RAW")
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

    private int columnLetterToIndex(String letter) {
        int index = 0;
        for (char c : letter.toCharArray()) {
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }
}
