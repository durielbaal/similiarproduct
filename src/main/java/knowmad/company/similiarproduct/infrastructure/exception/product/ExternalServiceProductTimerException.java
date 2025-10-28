package knowmad.company.similiarproduct.infrastructure.exception.product;

public class ExternalServiceProductTimerException extends RuntimeException {
  public ExternalServiceProductTimerException() {
    super(ProductErrorMessages.PRODUCT_TIME_OUT);
  }

}
