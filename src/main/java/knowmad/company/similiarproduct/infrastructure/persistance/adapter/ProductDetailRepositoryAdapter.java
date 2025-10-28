package knowmad.company.similiarproduct.infrastructure.persistance.adapter;


import io.netty.handler.timeout.ReadTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import knowmad.company.similiarproduct.domain.model.ProductDetail;
import knowmad.company.similiarproduct.domain.port.output.ProductDetailRepository;
import knowmad.company.similiarproduct.infrastructure.exception.product.ExternalServiceProductTimerException;
import knowmad.company.similiarproduct.infrastructure.exception.product.ProductIdNotValidException;
import knowmad.company.similiarproduct.infrastructure.exception.product.ProductNotFoundException;
import knowmad.company.similiarproduct.infrastructure.validator.ProductValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductDetailRepositoryAdapter implements ProductDetailRepository {

  private final WebClient webClient;

  @Value("${webclient.product.detail.url}")
  private String productDetailUrl;

  @Value("${webclient.product.similar.ids.url}")
  private String productSimilarIdsUrl;

  public ProductDetailRepositoryAdapter(WebClient webClient) {
    this.webClient = webClient;
  }

  /**
   * Fetches a Flux of similar product IDs for a given base product ID from an external service.
   * This method performs client-side validation, handles 404 (NotFound) and other
   * 4xx/5xx status codes, and maps timeout exceptions to a domain-specific exception.
   *
   * @param productId The ID of the base product.
   * @return A Flux of String representing similar product IDs.
   * @throws ProductIdNotValidException if the input product ID fails validation.
   * @throws ProductNotFoundException if the external service returns a 404 status.
   * @throws ExternalServiceProductTimerException if a read or connection timeout occurs.
   */
  @Override
  @Cacheable(value = "similarIds", key = "#productId")
  public Flux<String> findSimilarProductIds(String productId) {
    if (!ProductValidator.validateProductId(productId)) {
      return Flux.error(new ProductIdNotValidException());
    }

    return webClient.get()
        .uri(productSimilarIdsUrl, productId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
          if (clientResponse.statusCode().value() == 404) {
            return Mono.error(new ProductNotFoundException(productId));
          }
          return clientResponse.createException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, ClientResponse::createException)
        .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
        .onErrorMap(e -> {
          Throwable rootCause = (e.getCause() != null) ? e.getCause() : e;

          if (rootCause instanceof ReadTimeoutException ||
              rootCause instanceof TimeoutException) {

            return new ExternalServiceProductTimerException();
          }
          return e;
        })
        .flatMapMany(Flux::fromIterable);
  }

  /**
   * Fetches the detailed information for a single product ID from an external service.
   * similar to {@code findSimilarProductIds}, this method handles client-side
   * validation, HTTP status code translation (e.g., 404 to ProductNotFoundException),
   * and maps timeouts to {@code ExternalServiceProductTimerException}.
   *
   * @param productId The ID of the product to retrieve details for.
   * @return A Mono emitting the ProductDetail object.
   * @throws ProductIdNotValidException if the input product ID fails validation.
   * @throws ProductNotFoundException if the external service returns a 404 status.
   * @throws ExternalServiceProductTimerException if a read or connection timeout occurs.
   */
  @Override
  @Cacheable(value = "productDetails", key = "#productId")
  public Mono<ProductDetail> findProductDetail(String productId) {
    if(!ProductValidator.validateProductId(productId)){
      return Mono.error(new ProductIdNotValidException());
    }
    return webClient.get()
        .uri(productDetailUrl, productId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
          if (clientResponse.statusCode().value() == 404) {
            return Mono.error(new ProductNotFoundException(productId));
          }
          return clientResponse.createException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, ClientResponse::createException)
        .bodyToMono(ProductDetail.class)
        .onErrorMap(e -> {
          Throwable rootCause = (e.getCause() != null) ? e.getCause() : e;

          if (rootCause instanceof ReadTimeoutException ||
              rootCause instanceof TimeoutException) {

            return new ExternalServiceProductTimerException();
          }
          return e;
        });
  }
}