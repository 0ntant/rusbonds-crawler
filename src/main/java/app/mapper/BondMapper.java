package app.mapper;

import app.model.Bond;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class BondMapper
{
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    public static final int bonsFieldCount = Bond.class.getDeclaredFields().length;

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
                bond.getActualBalanceCount(),
                bond.getRating(),
                bond.getActivity(),
                bond.getAction(),
                bond.getBroker(),
                sysModifyDateStr
        );
    }

    public static Bond map(List<Object> bond)
    {
        if(bond.size() < bonsFieldCount)
        {
            fillObject(bond);
        }

        String isin = validateIsin(bond.get(3));
        return Bond.builder()
                .number(objectToInteger(bond.get(0)))
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
                .count(objectToInteger(bond.get(15)))
                .actualBalanceCount(objectToInteger(bond.get(16)))
                .rating((String) bond.get(17))
                .activity((String) bond.get(18))
                .action(validateAction(bond.get(19)))
                .broker((String)bond.get(20))
                .sysModifyDate(objectToLocalDate(bond.get(21)))
                .build();
    }

    private static String validateAction(Object action)
    {
        return String.valueOf(action)
                .split("\\.")[0];
    }

    private static void fillObject(List<Object> bond)
    {
        int objDelta = bonsFieldCount - bond.size();
        for (int i = 0; i < objDelta; i++)
        {
            bond.add("");
        }
    }

    private static String validateIsin(Object isin)
    {
        String isinStr = (String) isin;
        return isinStr
                .replace(" ", "");
    }

    private static LocalDate objectToLocalDate(Object value)
    {
        try
        {
            String valueStr = String.valueOf(value);
            if(isStringInvalid(valueStr))
            {
                return LocalDate.MIN;
            }
            else if (valueStr.contains("/"))
            {
                return LocalDate.parse(valueStr, formatter);
            }
            return convertExcelDateToLocalDate(Double.parseDouble(valueStr));
        }
        catch (Exception ex)
        {
            return LocalDate.MIN;
        }

    }
    public static LocalDate convertExcelDateToLocalDate(double excelDate)
    {
        LocalDate startDate = LocalDate.of(1899, 12, 30);
        return startDate.plusDays((long) excelDate);
    }

    private static Double finObjectToDouble(Object value)
    {
        String valueStr = value.toString();
        if(isStringInvalid(valueStr))
        {
            return (double) 0;
        }
        if (valueStr.contains("p"))
        {
            return objectToDouble(valueStr.substring(2));
        }
        return objectToDouble(valueStr);
    }

    private static Double objectToDouble(Object value)
    {
        String valueStr = value.toString();
        if(isStringInvalid(valueStr))
        {
            return (double) 0L;
        }
        return Double.valueOf(valueStr
                        .replace(",",".")
                .replaceAll("[^0-9.-]", "")
        );
    }

    private static Integer objectToInteger(Object value)
    {
        String valueStr = value.toString();
        if(isStringInvalid(valueStr))
        {
            return 0;
        }
        if(valueStr.length() == 1
                && !Character.isDigit(valueStr.charAt(0))
        )
        {
            return 0;
        }
        return Integer.valueOf(valueStr
                .split("\\.")[0]
                .replaceAll("[^0-9-]", "")
        );
    }

    private static boolean isStringInvalid(String valueStr)
    {
        return valueStr.isBlank()
                || valueStr.isEmpty()
                || valueStr.startsWith("#");
    }

    public static String modDateMap(LocalDate modifyDate)
    {
        return modifyDate.format(formatter);
    }

    public static LocalDate modDateMap(Object modifyDate)
    {
        return LocalDate.parse(modifyDate.toString(), formatter);
    }

    public static boolean isRatingMultiple(String rating)
    {
        return getPureRatings(rating).size() > 1;
    }

    public static List<String> getPureRatings(String ratings)
    {
        return Arrays
                .stream(ratings.split("/"))
                .map(BondMapper::pureRating)
                .toList();
    }

    public static String pureRating(String rating)
    {
        return rating.replaceAll("[^ABCD+-]", "");
    }
}
