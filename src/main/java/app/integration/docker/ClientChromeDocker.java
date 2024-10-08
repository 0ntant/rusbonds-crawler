package app.integration.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.net.http.HttpClient;
import java.time.Duration;

public class ClientChromeDocker
{
    DockerClient client;
    CreateContainerResponse containerChrome;

    public ClientChromeDocker()
    {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))  
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        client = DockerClientBuilder
                .getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }

    public void startChrome()
    {
        try
        {
            containerChrome= client.createContainerCmd("selenium/standalone-chromium:latest")
                    .withHostConfig(HostConfig.newHostConfig()
                            .withNetworkMode("host")
                            .withPortBindings(
                                    new PortBinding(Ports.Binding.bindPort(4444), new ExposedPort(4444)),  // -p 4444:4444
                                    new PortBinding(Ports.Binding.bindPort(7900), new ExposedPort(7900))
                            )
                            .withShmSize(2147483648L)
                    )
                    .withEnv("SE_VNC_NO_PASSWORD=true")
                    .withCmd("chromium")
                    .exec();
            client.startContainerCmd(containerChrome.getId()).exec();
            synchronized (Thread.currentThread()){Thread.currentThread().wait(1000 * 5);}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void stopChrome()
    {
        client.stopContainerCmd(containerChrome.getId()).exec();
        client.removeContainerCmd(containerChrome.getId()).exec();
    }
}
