package knowmad.company.similiarproduct.infrastructure.validator;

import java.util.regex.Pattern;

public final class ProductValidator {

  private ProductValidator() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static boolean validateProductId(String productId) {
    if (productId == null || !Pattern.matches("^\\d+$", productId.trim())) {
      return false;
    }
    int id = Integer.parseInt(productId.trim());
    if (id <= 0) {
      return false;
    }
    return true;
  }
}
