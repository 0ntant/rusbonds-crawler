package app.service;

import app.mapper.AccountingPolicyMapper;
import app.model.AccountingPolicy;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class AccountingPolicySheetService
{
    final String sheetName = "Sheet2";
    final int rowOffset = 2;
    final String rangeTemplate = "%s!A%s:B%s";

    String range;
    GoogleSheetService googleSheetService;

    public AccountingPolicySheetService()
    {
        this.googleSheetService = new GoogleSheetService();
        int maxRow = getMaxRow();
        this.range = String.format
                (
                        rangeTemplate,
                        sheetName,
                        rowOffset,
                        maxRow
                );
    }

    public AccountingPolicySheetService(GoogleSheetService googleSheetService)
    {
        this.googleSheetService = googleSheetService;
        int maxRow = getMaxRow();
        this.range = String.format
                (
                        rangeTemplate,
                        sheetName,
                        rowOffset,
                        maxRow
                );
    }

    public Map<String,Integer> getAllHash()
    {
        return AccountingPolicyMapper.mapHash(getAll());
    }

    public List<AccountingPolicy> getAll()
    {
        List<AccountingPolicy> bonds = new ArrayList<>();
        List<List<Object>> objects = googleSheetService.getAllTable(range);
        for(List<Object> objects1 : objects)
        {
            bonds.add(AccountingPolicyMapper.map(objects1));
        }
        return bonds;
    }

    public int getMaxRow()
    {
        return googleSheetService.findMaxRows(String.format("%s!A:A",sheetName)) - rowOffset + 1;
    }
}
