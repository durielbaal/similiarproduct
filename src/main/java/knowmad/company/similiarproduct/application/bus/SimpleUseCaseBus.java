package knowmad.company.similiarproduct.application.bus;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import knowmad.company.similiarproduct.application.useCase.UseCase;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

/**
 * Simple implementation of a UseCaseBus that maps input types (Commands/Queries)
 * to their corresponding UseCase handlers.
 */
@Component
public class SimpleUseCaseBus implements UseCaseBus {

  private final Map<Class<?>, UseCase<?, ?>> useCaseMap = new HashMap<>();

  /**
   * Initializes the bus by mapping all provided UseCase beans to their specific
   * input argument type (Command or Query).
   *
   * @param useCases A list of all UseCase implementations found in the Spring context.
   */
  public SimpleUseCaseBus(List<UseCase<?, ?>> useCases) {
    for (UseCase<?, ?> useCase : useCases) {
      // Resolves the first generic type argument of the UseCase class, which is the Input type (I).
      Class<?> inputType = Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(useCase.getClass(), UseCase.class))[0];
      useCaseMap.put(inputType, useCase);
    }
  }

  /**
   * Dispatches the given input (Command or Query) to the appropriate UseCase handler.
   *
   * @param input The input object (Command or Query) that defines the operation.
   * @param <I> The type of the input object.
   * @param <O> The type of the output object (result).
   * @return The output object returned by the executed UseCase.
   * @throws RuntimeException if no UseCase is registered for the given input type.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <I, O> O dispatch(I input) {
    UseCase<I, O> useCase = (UseCase<I, O>) useCaseMap.get(input.getClass());
    if (useCase == null) {
      throw new RuntimeException("No UseCase found for: " + input.getClass().getSimpleName());
    }
    return useCase.execute(input);
  }
}