package com.cloudprocure.supplier.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;

public record CatalogItemRequest(
        @NotBlank @Size(max = 80) String sku,
        @NotBlank @Size(max = 250) String name,
        @Size(max = 2000) String description,
        @NotBlank @Size(max = 100) String category,
        @NotBlank @Size(max = 30) String unit,
        @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal price,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency,
        Map<String, Object> attributes,
        boolean active
) {
}
