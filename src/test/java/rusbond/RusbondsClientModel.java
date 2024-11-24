package rusbond;

import app.exception.InvalidRusbondResponseException;
import app.integration.rusbonds.RusbondsClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RusbondsClientModel
{
    @Mock
    HttpClient client;

    ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    @InjectMocks
    RusbondsClient rusbondsClient = RusbondsClient.builder()
            .client(client)
            .accessToken("TestAccess")
            .refreshToken("TestRefresh")
            .spid("testSpid")
            .userAgent("testUserAgent")
            .build();

    @Test
    void getFindtoolId_get500CodeResponse_throwInvalidRusbondException() throws IOException, InterruptedException
    {
        //given
        String isin = "2001LLXR";
        HttpResponse<String> errorResponse =  HttpResponseMock.error(
                500,
                "Rusbond ERROR"
        );
        doReturn(errorResponse)
                .when(client)
                .send(any(), any());

        //then
        //expected
        InvalidRusbondResponseException exception = assertThrows(InvalidRusbondResponseException.class, () ->
                rusbondsClient.getFindtoolId(isin));
    }
}
