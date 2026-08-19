package com.cloudprocure.supplier.dto;

import com.cloudprocure.supplier.domain.CatalogItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CatalogItemResponse(UUID id, UUID supplierId, String sku, String name, String description,
                                  String category, String unit, BigDecimal price, String currency,
                                  Map<String, Object> attributes, boolean active,
                                  Instant createdAt, Instant updatedAt) {
    public static CatalogItemResponse from(CatalogItem item) {
        return new CatalogItemResponse(item.getId(), item.getSupplierId(), item.getSku(), item.getName(),
                item.getDescription(), item.getCategory(), item.getUnit(), item.getPrice(), item.getCurrency(),
                item.getAttributes(), item.isActive(), item.getCreatedAt(), item.getUpdatedAt());
    }
}
