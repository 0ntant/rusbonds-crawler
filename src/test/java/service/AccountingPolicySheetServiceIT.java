package service;

import app.model.AccountingPolicy;
import app.service.AccountingPolicySheetService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AccountingPolicySheetServiceIT
{
    AccountingPolicySheetService accountingPolicySheetServ = new AccountingPolicySheetService();

    @Test
    void getAllAccountingPolicies_success()
    {
        //given
        //then
        List<AccountingPolicy> accountingPoliciesTable = accountingPolicySheetServ.getAll();

        //expected
        assertTrue(!accountingPoliciesTable.isEmpty());
        assertEquals(accountingPoliciesTable.size(), 18);

        for(AccountingPolicy accountingPolicy : accountingPoliciesTable)
        {
            System.out.println(accountingPolicy.getCompanyRating()
                    + " "
                    + accountingPolicy.getLotCount());
        }
    }
}
