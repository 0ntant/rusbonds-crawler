package app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Bond
{
    Integer number;
    String issuerName;
    String paperName;
    String isin;
    String section;
    Double rate;
    Double yieldPurchasePricePercent;
    Double marketValuePurchaseTime;
    Double yieldNow;
    Double marketValueNow;
    Double bodyValueIncrease;
    LocalDate ticketExpirationDate;
    Double ticketLiveTimeYear;
    LocalDate paperRepaymentTime;
    Integer paymentRatePerYear;
    Integer count;
    String rating;
    String activity;
    LocalDate sysModifyDate;
    Integer action;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bond bond = (Bond) o;
        return Objects.equals(issuerName, bond.issuerName)
                && Objects.equals(paperName, bond.paperName) ;
    }
}
