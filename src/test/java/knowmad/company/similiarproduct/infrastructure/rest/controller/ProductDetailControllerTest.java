package knowmad.company.similiarproduct.infrastructure.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import knowmad.company.similiarproduct.application.query.GetProductDetailQuery;
import knowmad.company.similiarproduct.application.query.GetProductIdsQuery;
import knowmad.company.similiarproduct.domain.model.ProductDetail;
import knowmad.company.similiarproduct.domain.port.input.ProductDetailPort;
import knowmad.company.similiarproduct.domain.port.input.ProductIdsPort;
import knowmad.company.similiarproduct.infrastructure.rest.dto.ProductDetailResponse;
import knowmad.company.similiarproduct.infrastructure.rest.mapper.ProductMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ProductDetailController.class)
@Import(ProductDetailControllerTest.ControllerTestConfig.class)
class ProductDetailControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ProductDetailPort productDetailPort;

  @Autowired
  private ProductIdsPort productIdsPort;

  @Autowired
  private ProductMapper productMapper;

  private final String TEST_ID = "1";
  private final ProductDetail mockDomainDetail = new ProductDetail(
      TEST_ID, "T-Shirt", new BigDecimal("30.00"), true);
  private final ProductDetailResponse mockResponse = new ProductDetailResponse(
      "T-Shirt", new BigDecimal("30.00"), true);

  private final List<String> similarIds = Arrays.asList("2", "3");

  private final ProductDetail mockDomainDetail2 = new ProductDetail(
      "2", "Pants", new BigDecimal("50.00"), true);
  private final ProductDetailResponse mockResponse2 = new ProductDetailResponse(
      "Pants", new BigDecimal("50.00"), true);
  private final ProductDetailResponse mockResponse3 = new ProductDetailResponse(
      "Jacket", new BigDecimal("150.00"), false);


  @TestConfiguration
  static class ControllerTestConfig {
    @Bean
    public ProductDetailPort productDetailPort() {
      return mock(ProductDetailPort.class);
    }

    @Bean
    public ProductIdsPort productIdsPort() {
      return mock(ProductIdsPort.class);
    }

    @Bean
    public ProductMapper productMapper() {
      return mock(ProductMapper.class);
    }
  }

  @Test
  void getProductDetail_shouldReturnDetailOnSuccess() {
    when(productDetailPort.execute(any(GetProductDetailQuery.class)))
        .thenReturn(Mono.just(mockDomainDetail));

    when(productMapper.toResponse(any(ProductDetail.class)))
        .thenReturn(mockResponse);

    webTestClient.get().uri("/product/{productId}", TEST_ID)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ProductDetailResponse.class)
        .isEqualTo(mockResponse);
  }

  @Test
  void getProductDetail_shouldReturnNotFoundOnEmptyMono() {
    when(productDetailPort.execute(any(GetProductDetailQuery.class)))
        .thenReturn(Mono.empty());

    webTestClient.get().uri("/product/{productId}", TEST_ID)
        .exchange()
        .expectStatus().isOk();
  }


  @Test
  void getProductIds_shouldDiagnoseTextPlainBody() {
    List<String> similarIds = Arrays.asList("2", "3");
    String expectedConcatenation = "23";
    when(productIdsPort.execute(any(GetProductIdsQuery.class)))
        .thenReturn(Flux.fromIterable(similarIds));

    webTestClient.get().uri("/product/{productId}/similarids", TEST_ID)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .consumeWith(response -> {
          String actualBody = response.getResponseBody();
          Assertions.assertEquals(expectedConcatenation, actualBody,
              "El cuerpo devuelto como text/plain no es la concatenación esperada.");
        });
  }
  @Test
  void getProductIds_shouldReturnEmptyArrayOnEmptyFlux() {
    when(productIdsPort.execute(any(GetProductIdsQuery.class)))
        .thenReturn(Flux.empty());

    webTestClient.get().uri("/product/{productId}/similarids", TEST_ID)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(String.class)
        .hasSize(0);
  }

  @Test
  void getSimilarProductDetails_shouldReturnFluxOfDetailsOnSuccess() {
    when(productIdsPort.execute(any(GetProductIdsQuery.class)))
        .thenReturn(Flux.fromIterable(similarIds));
    when(productDetailPort.execute(any(GetProductDetailQuery.class)))
        .thenReturn(Mono.just(mockDomainDetail2))
        .thenReturn(Mono.just(mockDomainDetail));

    when(productMapper.toResponse(mockDomainDetail2)).thenReturn(mockResponse2);
    when(productMapper.toResponse(mockDomainDetail)).thenReturn(mockResponse3);

    webTestClient.get().uri("/product/{productId}/similar", TEST_ID)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ProductDetailResponse.class)
        .contains(mockResponse2, mockResponse3);
  }

  @Test
  void getSimilarProductDetails_shouldHandleMissingDetails() {
    when(productIdsPort.execute(any(GetProductIdsQuery.class)))
        .thenReturn(Flux.fromIterable(similarIds));

    when(productDetailPort.execute(any(GetProductDetailQuery.class)))
        .thenReturn(Mono.just(mockDomainDetail2))
        .thenReturn(Mono.empty());

    when(productMapper.toResponse(mockDomainDetail2)).thenReturn(mockResponse2);

    webTestClient.get().uri("/product/{productId}/similar", TEST_ID)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ProductDetailResponse.class)
        .contains(mockResponse2);
  }
}