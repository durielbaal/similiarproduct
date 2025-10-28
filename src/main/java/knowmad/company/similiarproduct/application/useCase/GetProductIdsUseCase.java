package knowmad.company.similiarproduct.application.useCase;


import knowmad.company.similiarproduct.application.query.GetProductIdsQuery;
import knowmad.company.similiarproduct.domain.port.input.ProductIdsPort;
import knowmad.company.similiarproduct.domain.port.output.ProductDetailRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
@Component
public class GetProductIdsUseCase implements UseCase<GetProductIdsQuery, Flux<String>>, ProductIdsPort {
  private final ProductDetailRepository productDetailRepository;

  public GetProductIdsUseCase(ProductDetailRepository productDetailRepository) {
    this.productDetailRepository = productDetailRepository;

  }

  @Override
  public Flux<String> execute(GetProductIdsQuery input) {
    return productDetailRepository.findSimilarProductIds(input.id());
  }
}
