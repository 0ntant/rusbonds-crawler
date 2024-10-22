package app.mapper;

import app.model.AccountingPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountingPolicyMapper
{
    public static AccountingPolicy map(List<Object> accountPolicy)
    {
        return AccountingPolicy.builder()
                .companyRating((String) accountPolicy.get(0))
                .lotCount(Integer.valueOf((String) accountPolicy.get(1)))
                .build();
    }

    public static List<Object> map(AccountingPolicy accountingPolicy)
    {
        return List.of(
                accountingPolicy.getCompanyRating(),
                accountingPolicy.getLotCount()
        );
    }

    public static Map<String, Integer> mapHash(List<AccountingPolicy> accountingPolicies)
    {
        Map<String, Integer> accountingPolicyMap = new HashMap<>();
        for(AccountingPolicy accountingPolicy : accountingPolicies)
        {
            accountingPolicyMap.put(
                    accountingPolicy.getCompanyRating(),
                    accountingPolicy.getLotCount()
            );
        }
        return accountingPolicyMap;
    }
}
