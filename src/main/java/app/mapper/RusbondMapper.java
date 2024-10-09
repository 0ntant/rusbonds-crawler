package app.mapper;

import com.fasterxml.jackson.databind.JsonNode;

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
}
