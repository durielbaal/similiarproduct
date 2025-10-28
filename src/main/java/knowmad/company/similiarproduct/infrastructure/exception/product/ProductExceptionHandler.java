package knowmad.company.similiarproduct.infrastructure.exception.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for product-related errors across the application's REST controllers.
 * This class uses {@code @RestControllerAdvice} to intercept specific exceptions
 * and return structured {@code ResponseEntity} objects.
 */
@RestControllerAdvice
public class ProductExceptionHandler {

  /**
   * Handles the {@code ProductNotFoundException} and returns a 200 OK status
   * with the exception message in the response body.
   *
   * @param ex The ProductNotFoundException instance.
   * @return A ResponseEntity containing the exception message and HTTP status 200 OK.
   */
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
    return ResponseEntity.ok(ex.getMessage());
  }

  /**
   * Handles the {@code ProductIdNotValidException} when an invalid product ID is provided.
   * Returns a 200 OK status with the validation failure message.
   *
   * @param ex The ProductIdNotValidException instance.
   * @return A ResponseEntity containing the exception message and HTTP status 200 OK.
   */
  @ExceptionHandler(ProductIdNotValidException.class)
  public ResponseEntity<String> handleProductIdNotValid(ProductIdNotValidException ex) {
    return ResponseEntity.ok(ex.getMessage());
  }

  /**
   * Handles the {@code ExternalServiceProductTimerException}, typically used when an
   * external service call times out or fails due to a long delay.
   * Returns a 200 OK status with the timeout message.
   *
   * @param ex The ExternalServiceProductTimerException instance.
   * @return A ResponseEntity containing the timeout message and HTTP status 200 OK.
   */
  @ExceptionHandler(ExternalServiceProductTimerException.class)
  public ResponseEntity<String> handleExternalServiceTimeout(ExternalServiceProductTimerException ex) {
    return ResponseEntity.ok(ex.getMessage());
  }

}