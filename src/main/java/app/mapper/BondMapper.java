package app.mapper;

import app.model.Bond;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BondMapper {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    public static List<Object> map(Bond bond)
    {
        String ticketExpirationDateStr = bond.getTicketExpirationDate().format(formatter);
        String paperRepaymentTimeStr = bond.getPaperRepaymentTime().format(formatter);
        String sysModifyDateStr = bond.getSysModifyDate().format(formatter);
        return List.of(
                bond.getNumber(),
                bond.getIssuerName(),
                bond.getPaperName(),
                bond.getIsin(),
                bond.getSection(),
                bond.getRate(),
                bond.getYieldPurchasePricePercent(),
                bond.getMarketValuePurchaseTime(),
                bond.getYieldNow(),
                bond.getMarketValueNow(),
                bond.getBodyValueIncrease(),
                ticketExpirationDateStr,
                bond.getTicketLiveTimeYear(),
                paperRepaymentTimeStr,
                bond.getPaymentRatePerYear(),
                bond.getCount(),
                bond.getRating(),
                bond.getActivity(),
                sysModifyDateStr
        );
    }

    public static Bond map(List<Object> bond)
    {
        String isin = validateIsin(bond.get(3));
        return Bond.builder()
                .number(Integer.valueOf((String) bond.get(0)))
                .issuerName((String) bond.get(1))
                .paperName((String) bond.get(2))
                .isin(isin)
                .section((String) bond.get(4))
                .rate(objectToDouble(bond.get(5)))
                .yieldPurchasePricePercent(objectToDouble(bond.get(6)))
                .marketValuePurchaseTime(finObjectToDouble(bond.get(7)))
                .yieldNow(objectToDouble(bond.get(8)))
                .marketValueNow(finObjectToDouble(bond.get(9)))
                .bodyValueIncrease(objectToDouble(bond.get(10)))
                .ticketExpirationDate(objectToLocalDate(bond.get(11)))
                .ticketLiveTimeYear(objectToDouble(bond.get(12)))
                .paperRepaymentTime(objectToLocalDate(bond.get(13)))
                .paymentRatePerYear(objectToInteger(bond.get(14)))
                .count(Integer.valueOf((String) bond.get(15)))
                .rating((String) bond.get(16))
                .activity((String) bond.get(17))
                .sysModifyDate(objectToLocalDate(bond.get(18)))
                .build();
    }

    private static String validateIsin(Object isin)
    {
        String isinStr = (String) isin;
        return isinStr.replace(" ", "");
    }

    private static LocalDate objectToLocalDate(Object value)
    {
        String valueStr = value.toString();
        if(valueStr.isEmpty() || valueStr.isBlank())
        {
            return LocalDate.MIN;
        }
        return LocalDate.parse(valueStr, formatter);
    }

    private static Double finObjectToDouble(Object value)
    {
        String string = value.toString();
        return objectToDouble(string.substring(2));
    }

    private static Double objectToDouble(Object value)
    {
        String valueStr = value.toString();
        if(valueStr.isBlank() || valueStr.isEmpty())
        {
            return (double) 0L;
        }
        return Double.valueOf(valueStr.replace(",", "."));
    }

    private static Integer objectToInteger(Object value)
    {
        String valueStr = value.toString();
        if(valueStr.isBlank() || valueStr.isEmpty())
        {
            return 0;
        }
        return Integer.valueOf(valueStr);
    }

    public static String modDateMap(LocalDate modifyDate)
    {
        return modifyDate.format(formatter);
    }

    public static LocalDate modDateMap(Object modifyDate)
    {
        return LocalDate.parse(modifyDate.toString(), formatter);
    }
}
