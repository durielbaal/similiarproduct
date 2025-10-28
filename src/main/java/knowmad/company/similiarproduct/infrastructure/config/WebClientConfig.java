package knowmad.company.similiarproduct.infrastructure.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Configuration class for setting up and configuring the WebClient bean.
 * This class applies custom settings for connection timeouts and response timeouts
 * using Reactor Netty's HttpClient.
 */
@Configuration
public class WebClientConfig {

  @Value("${webclient.mock.api.base-url}")
  private String mockApiBaseUrl;

  @Value("${webclient.request.timeout}")
  private int timeout;

  /**
   * Configures and creates a WebClient bean with custom connection and response timeouts.
   * The configuration includes:
   * A fixed connection timeout of 5000 milliseconds (5 seconds).
   * A response timeout dynamically configured via the {@code webclient.request.timeout} property.
   * The base URL for the WebClient is set using the {@code webclient.mock.api.base-url} property.
   *
   *
   * @return The configured WebClient instance.
   */
  @Bean
  public WebClient webClient() {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .responseTimeout(Duration.ofSeconds(timeout));

    ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    return WebClient.builder()
        .baseUrl(mockApiBaseUrl)
        .clientConnector(connector)
        .build();
  }
}