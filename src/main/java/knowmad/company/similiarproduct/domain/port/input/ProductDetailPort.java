package knowmad.company.similiarproduct.domain.port.input;


import knowmad.company.similiarproduct.application.query.GetProductDetailQuery;
import knowmad.company.similiarproduct.domain.model.ProductDetail;
import reactor.core.publisher.Mono;

public interface ProductDetailPort {
  public Mono<ProductDetail> execute(GetProductDetailQuery input);

}
