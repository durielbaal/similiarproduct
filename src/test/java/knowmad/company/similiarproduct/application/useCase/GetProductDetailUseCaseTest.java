package knowmad.company.similiarproduct.application.useCase;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.math.BigDecimal;
import knowmad.company.similiarproduct.application.query.GetProductDetailQuery;
import knowmad.company.similiarproduct.domain.model.ProductDetail;
import knowmad.company.similiarproduct.domain.port.output.ProductDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetProductDetailUseCaseTest {

  @Mock
  private ProductDetailRepository productDetailRepository;

  @InjectMocks
  private GetProductDetailUseCase useCase;

  @Test
  void execute_shouldCallRepositoryAndReturnProductDetail() {
    String testId = "123";
    GetProductDetailQuery query = new GetProductDetailQuery(testId);
    ProductDetail mockDetail = new ProductDetail(testId, "Test Product", new BigDecimal("50.0"), true);

    when(productDetailRepository.findProductDetail(testId))
        .thenReturn(Mono.just(mockDetail));

    StepVerifier.create(useCase.execute(query))
        .expectNext(mockDetail)
        .verifyComplete();

    verify(productDetailRepository, times(1)).findProductDetail(testId);
  }

  @Test
  void execute_shouldReturnEmptyMonoWhenRepositoryReturnsEmpty() {
    String testId = "999";
    GetProductDetailQuery query = new GetProductDetailQuery(testId);

    when(productDetailRepository.findProductDetail(testId))
        .thenReturn(Mono.empty());

    StepVerifier.create(useCase.execute(query))
        .verifyComplete();

    verify(productDetailRepository, times(1)).findProductDetail(testId);
  }

  @Test
  void execute_shouldPropagateErrorFromRepository() {
    String testId = "500";
    GetProductDetailQuery query = new GetProductDetailQuery(testId);
    RuntimeException mockError = new RuntimeException("Simulated External Error");

    when(productDetailRepository.findProductDetail(testId))
        .thenReturn(Mono.error(mockError));

    StepVerifier.create(useCase.execute(query))
        .expectError(RuntimeException.class)
        .verify();

    verify(productDetailRepository, times(1)).findProductDetail(testId);
  }
}