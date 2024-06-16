package edu.kpi.iasa.diplomaplugin.google.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {


    @Autowired
    private GoogleAuthorizationConfig googleAuthorizationConfig;

    public String createSpreadsheet(String title) throws GeneralSecurityException, IOException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();

        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(title));

        spreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute();

        SheetProperties sheetProperties = new SheetProperties().setTitle("Sheet1");
        AddSheetRequest addSheetRequest = new AddSheetRequest().setProperties(sheetProperties);
        Request request = new Request().setAddSheet(addSheetRequest);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(request));

        sheetsService.spreadsheets().batchUpdate(spreadsheet.getSpreadsheetId(), batchUpdateRequest).execute();

        ValueRange header = new ValueRange()
                .setValues(Collections.singletonList(Collections.singletonList("ПІБ")));

        sheetsService.spreadsheets().values()
                .update(spreadsheet.getSpreadsheetId(), "Sheet1!A1", header)
                .setValueInputOption("USER_ENTERED")
                .execute();

        return spreadsheet.getSpreadsheetId();
    }

    public void addRow(String name, String spreadsheetId) throws GeneralSecurityException, IOException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();

        ValueRange appendBody = new ValueRange()
                .setValues((List.of(
						Arrays.asList(name))));

        sheetsService.spreadsheets().values()
                .append(spreadsheetId, "Sheet1", appendBody)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    public int getRowCount(String spreadsheetId) throws GeneralSecurityException, IOException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, "Sheet1!A:A")
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            return 0;
        }

        return values.size();
    }

    public int calculateSumOfGrades(String spreadsheetId, int rowIndex) throws GeneralSecurityException, IOException {
        Sheets sheetsService = googleAuthorizationConfig.getSheetsService();

        String range = "Sheet1!A" + (rowIndex) + ":Z" + (rowIndex);

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> rowData = response.getValues();

        if (rowData == null || rowData.isEmpty()) {
            return 0;
        }

        int sum = 0;
        List<Object> row = rowData.get(0);
        for (int i = 1; i < row.size(); i++) {
            Object cell = row.get(i);
            if (cell instanceof String) {
                try {
                    sum += Integer.parseInt((String) cell);
                } catch (NumberFormatException e) {
                }
            } else if (cell instanceof Number) {
                sum += ((Number) cell).intValue();
            }
        }
        return sum;
    }
}