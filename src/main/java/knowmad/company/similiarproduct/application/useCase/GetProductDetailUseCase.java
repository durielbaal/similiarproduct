package knowmad.company.similiarproduct.application.useCase;


import knowmad.company.similiarproduct.application.query.GetProductDetailQuery;
import knowmad.company.similiarproduct.domain.model.ProductDetail;
import knowmad.company.similiarproduct.domain.port.input.ProductDetailPort;
import knowmad.company.similiarproduct.domain.port.output.ProductDetailRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
public class GetProductDetailUseCase implements UseCase<GetProductDetailQuery, Mono<ProductDetail>>, ProductDetailPort {
  private final ProductDetailRepository productDetailRepository;

  public GetProductDetailUseCase(ProductDetailRepository productDetailRepository) {
    this.productDetailRepository = productDetailRepository;
  }

  @Override
  public Mono<ProductDetail> execute(GetProductDetailQuery input) {
    return productDetailRepository.findProductDetail(input.id());
  }
}
