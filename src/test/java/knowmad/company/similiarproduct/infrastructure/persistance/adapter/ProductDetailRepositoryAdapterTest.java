package knowmad.company.similiarproduct.infrastructure.persistance.adapter;

import io.netty.channel.ChannelOption;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import knowmad.company.similiarproduct.infrastructure.exception.product.ExternalServiceProductTimerException;
import knowmad.company.similiarproduct.infrastructure.exception.product.ProductIdNotValidException;
import knowmad.company.similiarproduct.infrastructure.exception.product.ProductNotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

class ProductDetailRepositoryAdapterTest {

  private static MockWebServer mockWebServer;
  private ProductDetailRepositoryAdapter adapter;
  private static final String PRODUCT_ID = "123";

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @BeforeEach
  void initialize() {
    String baseUrl = mockWebServer.url("/").toString();

    WebClient webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build();

    adapter = new ProductDetailRepositoryAdapter(webClient);

    setFieldValue(adapter, "productDetailUrl", "/product/detail/{id}");
    setFieldValue(adapter, "productSimilarIdsUrl", "/product/{id}/similar");
  }

  private void setFieldValue(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  // --- Tests for findProductDetail (Mono<ProductDetail>) ---

  @Test
  void findProductDetail_shouldReturnProductDetailOnSuccess() {
    String jsonBody = "{\"id\":\"" + PRODUCT_ID + "\",\"name\":\"Test\",\"price\":10.0,\"availability\":true}";
    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody(jsonBody));

    StepVerifier.create(adapter.findProductDetail(PRODUCT_ID))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void findProductDetail_shouldThrowProductNotFoundExceptionOn404() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    StepVerifier.create(adapter.findProductDetail(PRODUCT_ID))
        .expectError(ProductNotFoundException.class)
        .verify();
  }

  @Test
  void findProductDetail_shouldPropagateErrorOn500() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    StepVerifier.create(adapter.findProductDetail(PRODUCT_ID))
        .expectError()
        .verify();
  }

  @Test
  void findProductDetail_shouldThrowExternalServiceProductTimerExceptionOnTimeout() {
    // Enqueue response with a delay (6s) greater than WebClientConfig's timeout (5s)
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json") // Necessary to avoid UnsupportedMediaTypeException
        .setBody("{}")
        .setBodyDelay(6, TimeUnit.SECONDS)
    );

    // Recreate WebClient with the 5-second timeout from WebClientConfig
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .responseTimeout(Duration.ofSeconds(5));

    WebClient clientWithTimeout = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();

    ProductDetailRepositoryAdapter adapterWithTimeout = new ProductDetailRepositoryAdapter(clientWithTimeout);
    setFieldValue(adapterWithTimeout, "productDetailUrl", "/product/detail/{id}");

    StepVerifier.create(adapterWithTimeout.findProductDetail(PRODUCT_ID))
        .expectError(ExternalServiceProductTimerException.class)
        .verify(Duration.ofSeconds(8));
  }

  @Test
  void findProductDetail_shouldThrowProductIdNotValidExceptionOnValidationFailure() {
    StepVerifier.create(adapter.findProductDetail(null))
        .expectError(ProductIdNotValidException.class)
        .verify();
  }

  // --- Tests for findSimilarProductIds (Flux<String>) ---

  @Test
  void findSimilarProductIds_shouldReturnFluxOfIdsOnSuccess() {
    String jsonBody = "[\"2\",\"3\",\"4\"]";
    mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody(jsonBody));

    StepVerifier.create(adapter.findSimilarProductIds(PRODUCT_ID))
        .expectNext("2", "3", "4")
        .verifyComplete();
  }

  @Test
  void findSimilarProductIds_shouldThrowProductNotFoundExceptionOn404() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    StepVerifier.create(adapter.findSimilarProductIds(PRODUCT_ID))
        .expectError(ProductNotFoundException.class)
        .verify();
  }

  @Test
  void findSimilarProductIds_shouldThrowProductIdNotValidExceptionOnValidationFailure() {
    StepVerifier.create(adapter.findSimilarProductIds(null))
        .expectError(ProductIdNotValidException.class)
        .verify();
  }

  @Test
  void findSimilarProductIds_shouldThrowTimeoutExceptionOnReadTimeout() {
    // Enqueue response with a delay (6s) greater than WebClientConfig's timeout (5s)
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody("[]")
        .setBodyDelay(6, TimeUnit.SECONDS)
    );

    // Recreate WebClient with the 5-second timeout from WebClientConfig
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .responseTimeout(Duration.ofSeconds(5));

    WebClient clientWithTimeout = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();

    ProductDetailRepositoryAdapter adapterWithTimeout = new ProductDetailRepositoryAdapter(clientWithTimeout);
    setFieldValue(adapterWithTimeout, "productSimilarIdsUrl", "/product/{id}/similar");

    StepVerifier.create(adapterWithTimeout.findSimilarProductIds(PRODUCT_ID))
        .expectError(ExternalServiceProductTimerException.class)
        .verify(Duration.ofSeconds(8));
  }
}