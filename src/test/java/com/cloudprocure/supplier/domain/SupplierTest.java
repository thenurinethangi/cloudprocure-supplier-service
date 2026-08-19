package com.cloudprocure.supplier.domain;

import com.cloudprocure.supplier.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SupplierTest {
    private static final Instant NOW = Instant.parse("2026-08-18T04:00:00Z");

    @Test
    void createsNormalizedActiveSupplier() {
        Supplier supplier = supplier(SupplierStatus.ACTIVE);

        assertThat(supplier.getCode()).isEqualTo("TECH-001");
        assertThat(supplier.getStatus()).isEqualTo(SupplierStatus.ACTIVE);
        assertThat(supplier.getCategories()).containsExactly("IT_EQUIPMENT");
    }

    @Test
    void softDeleteRetainsSupplierAndMakesItInactive() {
        Supplier supplier = supplier(SupplierStatus.ACTIVE);

        supplier.deactivate(NOW.plusSeconds(10));

        assertThat(supplier.getStatus()).isEqualTo(SupplierStatus.INACTIVE);
        assertThat(supplier.getUpdatedAt()).isEqualTo(NOW.plusSeconds(10));
    }

    @Test
    void rejectsRatingOutsideZeroToFive() {
        assertThatThrownBy(() -> Supplier.create(UUID.randomUUID(), "TECH-001", "TechSource",
                "TechSource Lanka Ltd", "PV-100", SupplierStatus.ACTIVE, new BigDecimal("5.1"),
                Set.of("IT_EQUIPMENT"), null, null, null, NOW))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("rating");
    }

    @Test
    void rejectsNegativeCatalogPrice() {
        assertThatThrownBy(() -> CatalogItem.create(UUID.randomUUID(), UUID.randomUUID(), "LAP-001",
                "Laptop", null, "IT_EQUIPMENT", "EA", new BigDecimal("-1"), "LKR",
                java.util.Map.of(), true, NOW))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("price");
    }

    private Supplier supplier(SupplierStatus status) {
        return Supplier.create(UUID.randomUUID(), " tech-001 ", "TechSource", "TechSource Lanka Ltd",
                "PV-100", status, new BigDecimal("4.7"), Set.of("IT_EQUIPMENT"),
                List.of(new Contact("Kasun Perera", "Account Manager", "kasun@techsource.example",
                        "+94 11 555 0101", true)),
                new Address("100 Galle Road", null, "Colombo", "Western", "00300", "Sri Lanka"),
                "Preferred laptop supplier", NOW);
    }
}
