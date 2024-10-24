package service;

import app.model.AccountingPolicy;
import app.service.AccountingPolicySheetService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
        assertFalse(accountingPoliciesTable.isEmpty());
        assertEquals(accountingPoliciesTable.size(), 18);

        for(AccountingPolicy accountingPolicy : accountingPoliciesTable)
        {
            System.out.println(accountingPolicy.getCompanyRating()
                    + " "
                    + accountingPolicy.getLotCount());
        }
    }

    @Test
    void getAllAccountingPolicies_numericIndex()
    {
        //given
        List<AccountingPolicy> accPolicies = accountingPolicySheetServ.getAll() ;
        String rating = "BB+";
        String anotherRating = "AA";
        //then
        AccountingPolicy accountingPolicy = accPolicies
                .stream()
                .filter(i -> i.getCompanyRating().equals(rating))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("error"));

        AccountingPolicy anotherAccountingPolicy = accPolicies
                .stream()
                .filter(i -> i.getCompanyRating().equals(anotherRating))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("error"));

        //expected
        assertTrue(accPolicies.indexOf(accountingPolicy) > 0) ;
        assertTrue(accPolicies.indexOf(anotherAccountingPolicy) > accPolicies.indexOf(accountingPolicy) ) ;
    }


}
