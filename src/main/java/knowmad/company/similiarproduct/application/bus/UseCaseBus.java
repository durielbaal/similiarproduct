package knowmad.company.similiarproduct.application.bus;

public interface UseCaseBus {
  <I, O> O dispatch(I input);
}
