package app.mapper;

import app.model.BondRepayment;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RusbondMapper
{
    public static String accessToken(JsonNode json)
    {
        return json
                .path("accessToken")
                .asText();
    }

    public static int fintoolId(JsonNode json)
    {
        return json
                .path("entities")
                .get(0)
                .path("id")
                .asInt();
    }

    public static String isin(JsonNode json)
    {
        return json
                .path("entities")
                .get(0)
                .path("instrumentData")
                .path("isin")
                .asText();
    }

    public static int issuerId(JsonNode json)
    {
        return json
                .path("nameAndCodes")
                .path("issuerId")
                .asInt();
    }

    public static Double marketValueNow(JsonNode json)
    {
        return json
                .path("priceData")
                .path("lastPrice")
                .asDouble();
    }

    public static String ratingRu(JsonNode json)
    {
        return json
                .path("scorings")
                .get(0)
                .path("ratingRu")
                .asText();
    }

    public static List<BondRepayment> repayments(JsonNode json)
    {
        List<BondRepayment> bondRepayments = new ArrayList<>();
        for(JsonNode jsonNode : json)
        {
            bondRepayments.add(new BondRepayment(
                    LocalDate.parse(
                            jsonNode.path("payDate").asText().substring(0, 10)
                    ),
                    jsonNode.path("value").asDouble())
            );
        }
        return bondRepayments;
    }
}
