package knowmad.company.similiarproduct.infrastructure.exception.product;


public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(String id) {
    super(String.format(ProductErrorMessages.PRODUCT_NOT_FOUND_FORMAT, id));
  }

}
