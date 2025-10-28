package knowmad.company.similiarproduct.infrastructure.rest.controller;


import knowmad.company.similiarproduct.application.query.GetProductDetailQuery;
import knowmad.company.similiarproduct.application.query.GetProductIdsQuery;
import knowmad.company.similiarproduct.domain.model.ProductDetail;
import knowmad.company.similiarproduct.domain.port.input.ProductDetailPort;
import knowmad.company.similiarproduct.domain.port.input.ProductIdsPort;
import knowmad.company.similiarproduct.infrastructure.rest.dto.ProductDetailResponse;
import knowmad.company.similiarproduct.infrastructure.rest.mapper.ProductMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/product")
@Validated
public class ProductDetailController {

  private final ProductDetailPort productDetailPort;
  private final ProductIdsPort productIdsPort;
  private final ProductMapper productMapper;


  public ProductDetailController(ProductDetailPort productDetailPort,
      ProductIdsPort productIdsPort,
      ProductMapper productMapper) {
    this.productDetailPort = productDetailPort;
    this.productIdsPort = productIdsPort;
    this.productMapper = productMapper;
  }


  /**
   * Handles GET requests to retrieve the detailed information for a specific product.
   *
   * @param productId The ID of the product.
   * @return A Mono emitting the product details mapped to a response DTO.
   */
  @GetMapping("/{productId}")
  public Mono<ProductDetailResponse> getProductDetail(@PathVariable String productId) {
    GetProductDetailQuery query = new GetProductDetailQuery(productId);
    Mono<ProductDetail> productDetail = productDetailPort.execute(query);
    return productDetail.map(productMapper::toResponse);
  }

  /**
   * Handles GET requests to retrieve only the IDs of products considered similar
   * to the specified base product ID.
   *
   * @param productId The ID of the base product.
   * @return A Flux emitting the IDs (as String) of the similar products.
   */
  @GetMapping("/{productId}/similarids")
  public Flux<String> getProductIds(@PathVariable  String productId) {
    GetProductIdsQuery query = new GetProductIdsQuery(productId);
    return productIdsPort.execute(query);
  }

  /**
   * Handles GET requests to retrieve the full details for all products similar
   * to the specified base product ID.
   * This method chains two operations reactively: first fetching the similar IDs,
   * then using {@code flatMap} to fetch the detail for each ID concurrently.
   *
   * @param productId The ID of the base product.
   * @return A Flux emitting the detailed response DTOs for all similar products.
   */
  @GetMapping("/{productId}/similar")
  public Flux<ProductDetailResponse> getSimilarProductDetails(@PathVariable String productId) {
    return productIdsPort.execute(new GetProductIdsQuery(productId))
        .flatMap(id -> productDetailPort
            .execute(new GetProductDetailQuery(id))
            .map(productMapper::toResponse));
  }
}