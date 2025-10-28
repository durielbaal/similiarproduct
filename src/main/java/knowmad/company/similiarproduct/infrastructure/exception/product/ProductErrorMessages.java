package knowmad.company.similiarproduct.infrastructure.exception.product;

public final class ProductErrorMessages {
  private ProductErrorMessages() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  public static final String PRODUCT_NOT_FOUND_FORMAT = "Product %s not found";
  public static final String PRODUCT_NOT_VALID_FORMAT = "Product id must be a positive integer value, greater than zero";
  public static final String PRODUCT_TIME_OUT = "Timeout when trying to reach external product service";
}
