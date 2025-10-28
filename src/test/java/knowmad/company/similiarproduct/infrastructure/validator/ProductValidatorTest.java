package knowmad.company.similiarproduct.infrastructure.validator;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {


  @Test
  void constructor_shouldThrowUnsupportedOperationException() throws NoSuchMethodException {

    Constructor<ProductValidator> constructor = ProductValidator.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
    assertInstanceOf(UnsupportedOperationException.class, thrown.getCause());
    assertEquals("This is a utility class and cannot be instantiated", thrown.getCause().getMessage());
  }

  @Test
  void validateProductId_shouldReturnTrueForValidPositiveId() {
    assertTrue(ProductValidator.validateProductId("12345"));
    assertTrue(ProductValidator.validateProductId("1"));
    assertTrue(ProductValidator.validateProductId("99999"));
  }

  @Test
  void validateProductId_shouldReturnTrueForValidIdWithSpaces() {
    assertTrue(ProductValidator.validateProductId(" 54321 "));
    assertTrue(ProductValidator.validateProductId("1 "));
    assertTrue(ProductValidator.validateProductId(" 1"));
  }

  @Test
  void validateProductId_shouldReturnFalseForNullId() {
    assertFalse(ProductValidator.validateProductId(null));
  }

  @Test
  void validateProductId_shouldReturnFalseForEmptyOrBlankString() {
    assertFalse(ProductValidator.validateProductId(""));
    assertFalse(ProductValidator.validateProductId(" "));
    assertFalse(ProductValidator.validateProductId("\t\n"));
  }

  @Test
  void validateProductId_shouldReturnFalseForZeroOrNegativeId() {
    assertFalse(ProductValidator.validateProductId("0"));
    assertFalse(ProductValidator.validateProductId("-1"));
    assertFalse(ProductValidator.validateProductId(" -99 "));
  }

  @Test
  void validateProductId_shouldReturnFalseForNonNumericCharacters() {
    assertFalse(ProductValidator.validateProductId("A123"));
    assertFalse(ProductValidator.validateProductId("123B"));
    assertFalse(ProductValidator.validateProductId("1.2"));
    assertFalse(ProductValidator.validateProductId("1 2 3"));
    assertFalse(ProductValidator.validateProductId("product-1"));
  }

  @Test
  void validateProductId_shouldReturnFalseForMalformedNumericString() {
    assertFalse(ProductValidator.validateProductId("123L"));
  }
}
