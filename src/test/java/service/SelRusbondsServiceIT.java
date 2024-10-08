package service;

import app.integration.docker.ClientChromeDocker;
import app.service.SelRusbondsService;
import org.junit.jupiter.api.Test;

public class SelRusbondsServiceIT
{
    ClientChromeDocker clientChromeDocker;

    @Test
    void start_container()
    {
        clientChromeDocker = new ClientChromeDocker();

        clientChromeDocker.startChrome();
    }
}
