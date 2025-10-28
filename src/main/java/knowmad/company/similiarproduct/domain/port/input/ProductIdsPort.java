package knowmad.company.similiarproduct.domain.port.input;

import knowmad.company.similiarproduct.application.query.GetProductIdsQuery;
import reactor.core.publisher.Flux;

public interface ProductIdsPort {
  public Flux<String> execute(GetProductIdsQuery input);
}
