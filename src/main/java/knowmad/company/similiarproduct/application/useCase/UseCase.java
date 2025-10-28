package knowmad.company.similiarproduct.application.useCase;

public interface UseCase<I, O> {
  O execute(I input);
}
