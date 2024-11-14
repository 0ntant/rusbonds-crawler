package rusbond;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpResponseMock
{
    public static HttpResponse<String> error(int statusCode,
                                             String responseBody)
    {
        HttpResponse<String> errorResponse = mock(HttpResponse.class);

        when(errorResponse.statusCode()).thenReturn(statusCode);
        when(errorResponse.body()).thenReturn(responseBody);

        return errorResponse;
    }
}
