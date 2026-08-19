package com.cloudprocure.supplier.domain;

import com.cloudprocure.supplier.exception.BusinessRuleException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Document("catalog_items")
@CompoundIndex(name = "supplier_sku_unique", def = "{'supplierId': 1, 'sku': 1}", unique = true)
public class CatalogItem {
    @Id private UUID id;
    @Indexed private UUID supplierId;
    private String sku;
    @TextIndexed private String name;
    @TextIndexed private String description;
    @Indexed private String category;
    private String unit;
    private BigDecimal price;
    private String currency;
    private Map<String, Object> attributes;
    @Indexed private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    protected CatalogItem() {
    }

    public static CatalogItem create(UUID id, UUID supplierId, String sku, String name, String description,
                                     String category, String unit, BigDecimal price, String currency,
                                     Map<String, Object> attributes, boolean active, Instant now) {
        CatalogItem item = new CatalogItem();
        item.id = id;
        item.supplierId = supplierId;
        item.sku = required(sku, "SKU").toUpperCase();
        item.createdAt = now;
        item.update(name, description, category, unit, price, currency, attributes, active, now);
        return item;
    }

    public void update(String name, String description, String category, String unit, BigDecimal price,
                       String currency, Map<String, Object> attributes, boolean active, Instant now) {
        this.name = required(name, "Catalog item name");
        this.description = optional(description);
        this.category = required(category, "Catalog category").toUpperCase();
        this.unit = required(unit, "Catalog unit").toUpperCase();
        if (price == null || price.signum() < 0) {
            throw new BusinessRuleException("Catalog price cannot be negative");
        }
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.currency = required(currency, "Currency").toUpperCase();
        if (this.currency.length() != 3) {
            throw new BusinessRuleException("Currency must be a three-letter ISO code");
        }
        this.attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        this.active = active;
        this.updatedAt = now;
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new BusinessRuleException(field + " is required");
        return value.trim();
    }

    private static String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public UUID getId() { return id; }
    public UUID getSupplierId() { return supplierId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getUnit() { return unit; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }
    public Map<String, Object> getAttributes() { return attributes; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
