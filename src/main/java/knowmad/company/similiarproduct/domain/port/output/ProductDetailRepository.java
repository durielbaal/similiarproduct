package knowmad.company.similiarproduct.domain.port.output;

import knowmad.company.similiarproduct.domain.model.ProductDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductDetailRepository {
  public Flux<String> findSimilarProductIds(String productId);
  public Mono<ProductDetail> findProductDetail(String productId);
}
