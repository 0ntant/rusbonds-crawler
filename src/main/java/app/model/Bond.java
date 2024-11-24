package app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

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
    Integer actualBalanceCount;
    String rating;
    String activity;
    String action;
    String broker;
    LocalDate sysModifyDate;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bond bond = (Bond) o;
        return Objects.equals(issuerName, bond.issuerName)
                && Objects.equals(paperName, bond.paperName) ;
    }

    public void setCompTicketLiveTimeYear()
    {
        ticketLiveTimeYear =
                new BigDecimal(DAYS.between(ticketExpirationDate, LocalDate.now()))
                        .abs()
                        .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_EVEN)
                        .doubleValue();
    }

    public boolean isNeedUpdate()
    {
        if (getIsin().isEmpty() || getIsin().isBlank())
        {
            return false;
        }

        if (getSysModifyDate().isEqual(LocalDate.now()))
        {
            return false;
        }
        return true;
    }
}
