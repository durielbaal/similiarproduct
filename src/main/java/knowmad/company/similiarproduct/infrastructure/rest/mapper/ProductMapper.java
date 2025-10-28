package knowmad.company.similiarproduct.infrastructure.rest.mapper;


import knowmad.company.similiarproduct.domain.model.ProductDetail;
import knowmad.company.similiarproduct.infrastructure.rest.dto.ProductDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public ProductDetailResponse toResponse(ProductDetail productDetail) {
    if (productDetail == null) {
      return null;
    }
    return new ProductDetailResponse(
        productDetail.getName(),
        productDetail.getPrice(),
        productDetail.isAvailability()
    );
  }



}
