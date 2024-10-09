package app.integration.rusbonds;

import app.integration.dto.output.HintDto;
import app.integration.dto.output.RusbonCredDto;
import app.mapper.RusbondMapper;
import app.model.RusbondsKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;

import static app.config.RusbondsConfig.*;

@Slf4j
public class RusbondsClient
{
    HttpClient client;
    String accessToken;
    String refreshToken;
    String spid;
    String spsc;
    String userAgent;
    JsonMapper jsonMapper = new JsonMapper();

    public RusbondsClient()
    {
        this.client = HttpClient.newHttpClient();
        this.spid = COOKIE_SPID;
        this.spsc = COOKIE_SPSC;
        this.userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36";
    }

    public RusbondsClient(RusbondsKeys rusbondsKeys)
    {
        this.client = HttpClient.newHttpClient();
        setRusbondsKeys(rusbondsKeys);
    }

    public void setRusbondsKeys(RusbondsKeys rusbondsKeys)
    {
        this.spid = rusbondsKeys.getSpid();
        this.spsc = rusbondsKeys.getSpcs();
        this.userAgent = rusbondsKeys.getUserAgent();
        this.accessToken = rusbondsKeys.getAccessToken();
        this.refreshToken = rusbondsKeys.getRefreshToken();
    }

    public void login()
    {
        try
        {
            RusbonCredDto rusbonCredDto = new RusbonCredDto(
                    LOGIN,
                    PASSWORD
            );
            String json = jsonMapper.writeValueAsString(rusbonCredDto);
            HttpRequest request = getDefaultHeaders()
                    .uri(new URI("https://rusbonds.ru/api/v2/account/login"))
                    .POST(BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            logResponse(request, response);
            JsonNode jsonResponse = jsonMapper.readTree(response.body());
            this.accessToken = String.format("Bearer %s", RusbondMapper.accessToken(jsonResponse));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public JsonNode getFindtoolId(String isin)
    {
        try
        {
            int[] types={};
            HintDto hintDto = new HintDto(
                    isin,
                    1,
                    50,
                    types
            );
            String json = jsonMapper.writeValueAsString(hintDto);
            HttpRequest request = getDefaultHeaders()
                    .header("Authorization", accessToken)
                    .uri(new URI("https://rusbonds.ru/api/v2/search/hint"))
                    .POST(BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            logResponse(request, response);
           // JsonNode jsonResponse = jsonMapper.readTree(response.body());
           // return RusbondMapper.fintoolId(jsonResponse);
            return jsonMapper.readTree(response.body());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public JsonNode getIssuerId(int findtoolId)
    {
        try
        {
            String url = String.format("https://rusbonds.ru/api/v2/bond/%s",
                    findtoolId
            );
            HttpRequest request = getDefaultHeaders()
                    .header("Authorization", accessToken)
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            logResponse(request, response);
            //JsonNode jsonResponse = jsonMapper.readTree(response.body());
           // return RusbondMapper.issuerId(jsonResponse);
            return jsonMapper.readTree(response.body());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public JsonNode getMarketValueNow(int issuerId)
    {
        try
        {
            String url = String.format("https://rusbonds.ru/api/v2/bond/%s",
                    issuerId
            );
            HttpRequest request = getDefaultHeaders()
                    .header("Authorization", accessToken)
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            logResponse(request, response);
           // JsonNode responseJson = jsonMapper.readTree(response.body());
           //  return RusbondMapper.marketValueNow(responseJson);
            return jsonMapper.readTree(response.body());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void executeEmptyRequests(int fintoolId)
    {
        emptyRequest("https://rusbonds.ru/api/v2/rusbonds/common/tariffs", fintoolId);
        emptyRequest("https://rusbonds.ru/api/v2/rusbonds/common/markers", fintoolId);
        emptyRequest("https://rusbonds.ru/api/v2/rusbonds/common/banners", fintoolId);
    }

    public JsonNode getRating(int issuerId)
    {
        Random rand = new Random();
        try
        {
            synchronized (Thread.currentThread())
            {
                Thread.currentThread().wait(rand.nextInt(20, 30));
            }
            String url = String.format("https://rusbonds.ru/api/v2/issuer/%s/risks",
                    issuerId
            );

            HttpRequest request = getDefaultHeaders()
                    .header("Authorization", accessToken)
                    .uri(new URI(url))
                    .GET()
                    .build();
            executeEmptyRequests(issuerId);
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            logResponse(request, response);
           // JsonNode responseJson = jsonMapper.readTree(response.body());
           // return RusbondMapper.ratingRu(responseJson);

            return jsonMapper.readTree(response.body());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void emptyRequest(String url, int fintoolId)
    {
        try
        {
            HttpRequest request = getDefaultHeaders()
                    .header("Authorization", accessToken)
                    .header("Referer", String.format("https://rusbonds.ru/bonds/%s/", fintoolId))
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void logResponse(
            HttpRequest request,
            HttpResponse<String> response
    )
    {
        log.info("[{}] {} | ResponseCode: {}",
                request.method(),
                request.uri(),
                response.statusCode()
        );
    }

    private Builder getDefaultHeaders()
    {
        return HttpRequest.newBuilder()
                .version(Version.HTTP_2)
                .header("Cookie", getCookieValue())
                .header("Content-type","application/json")
                .header("User-Agent", userAgent);
    }

    private String getCookieValue_()
    {
        return String.format("spid=; spsc=; auth.strategy=local; auth._token.local=%s; auth._refresh_token.local=%s; auth.expTime=240; spid=%s; spsc=%s",
                accessToken,
                refreshToken,
                spid,
                spsc
        );
    }

    private String getCookieValue()
    {
        return String.format(
                "spid=%s; spsc=%s",
                spid,
                spsc
        );
    }
}
