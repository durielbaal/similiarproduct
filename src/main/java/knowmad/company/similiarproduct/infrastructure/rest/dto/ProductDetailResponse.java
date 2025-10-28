package knowmad.company.similiarproduct.infrastructure.rest.dto;

import java.math.BigDecimal;

public record ProductDetailResponse(String name, BigDecimal price, boolean availability) {}
