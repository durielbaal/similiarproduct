package knowmad.company.similiarproduct.application.useCase;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.Arrays;
import java.util.List;
import knowmad.company.similiarproduct.application.query.GetProductIdsQuery;
import knowmad.company.similiarproduct.domain.port.output.ProductDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetProductIdsUseCaseTest {

  @Mock
  private ProductDetailRepository productDetailRepository;

  @InjectMocks
  private GetProductIdsUseCase useCase;

  @Test
  void execute_shouldCallRepositoryAndReturnSimilarIds() {
    String testId = "123";
    GetProductIdsQuery query = new GetProductIdsQuery(testId);
    List<String> mockIds = Arrays.asList("2", "3", "4");

    when(productDetailRepository.findSimilarProductIds(testId))
        .thenReturn(Flux.fromIterable(mockIds));

    StepVerifier.create(useCase.execute(query))
        .expectNext("2", "3", "4")
        .verifyComplete();

    verify(productDetailRepository, times(1)).findSimilarProductIds(testId);
  }

  @Test
  void execute_shouldReturnEmptyFluxWhenRepositoryReturnsEmpty() {
    String testId = "999";
    GetProductIdsQuery query = new GetProductIdsQuery(testId);

    when(productDetailRepository.findSimilarProductIds(testId))
        .thenReturn(Flux.empty());
    StepVerifier.create(useCase.execute(query))
        .verifyComplete();

    verify(productDetailRepository, times(1)).findSimilarProductIds(testId);
  }

  @Test
  void execute_shouldPropagateErrorFromRepository() {
    String testId = "500";
    GetProductIdsQuery query = new GetProductIdsQuery(testId);
    RuntimeException mockError = new RuntimeException("Simulated DB Error");

    when(productDetailRepository.findSimilarProductIds(testId))
        .thenReturn(Flux.error(mockError));

    StepVerifier.create(useCase.execute(query))
        .expectError(RuntimeException.class)
        .verify();

    verify(productDetailRepository, times(1)).findSimilarProductIds(testId);
  }
}