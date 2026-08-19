package com.cloudprocure.supplier.service;

import com.cloudprocure.supplier.domain.Supplier;
import com.cloudprocure.supplier.domain.SupplierStatus;
import com.cloudprocure.supplier.dto.CatalogItemRequest;
import com.cloudprocure.supplier.dto.CreateSupplierRequest;
import com.cloudprocure.supplier.exception.BusinessRuleException;
import com.cloudprocure.supplier.repository.CatalogItemRepository;
import com.cloudprocure.supplier.repository.SupplierRepository;
import com.cloudprocure.supplier.activity.ActivityPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {
    private static final Instant NOW = Instant.parse("2026-08-18T04:00:00Z");

    @Mock SupplierRepository supplierRepository;
    @Mock CatalogItemRepository catalogItemRepository;
    @Mock ActivityPublisher activityPublisher;
    @Mock ActorProvider actorProvider;
    private SupplierService service;

    @BeforeEach
    void setUp() {
        service = new SupplierService(supplierRepository, catalogItemRepository,
                Clock.fixed(NOW, ZoneOffset.UTC), activityPublisher, actorProvider);
    }

    @Test
    void duplicateSupplierCodeIsRejected() {
        when(supplierRepository.existsByCodeIgnoreCase("TECH-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(createSupplier()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("code");

        verify(supplierRepository, never()).save(any());
    }

    @Test
    void activeCatalogItemRequiresActiveSupplier() {
        UUID supplierId = UUID.randomUUID();
        Supplier inactive = Supplier.create(supplierId, "OLD-001", "Old Supplier", null, null,
                SupplierStatus.INACTIVE, null, Set.of("OFFICE"), null, null, null, NOW);
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> service.createCatalogItem(supplierId, catalogRequest(true)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("active supplier");

        verify(catalogItemRepository, never()).save(any());
    }

    @Test
    void softDeleteAlsoDeactivatesSupplierCatalog() {
        UUID supplierId = UUID.randomUUID();
        Supplier active = Supplier.create(supplierId, "TECH-001", "TechSource", null, null,
                SupplierStatus.ACTIVE, null, Set.of("IT_EQUIPMENT"), null, null, null, NOW);
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(active));

        service.delete(supplierId);

        verify(catalogItemRepository).deactivateAllBySupplierId(supplierId, NOW);
    }

    private CreateSupplierRequest createSupplier() {
        return new CreateSupplierRequest("TECH-001", "TechSource", "TechSource Lanka Ltd", "PV-100",
                SupplierStatus.ACTIVE, new BigDecimal("4.7"), Set.of("IT_EQUIPMENT"),
                null, null, "Preferred");
    }

    private CatalogItemRequest catalogRequest(boolean active) {
        return new CatalogItemRequest("LAP-001", "Laptop", null, "IT_EQUIPMENT", "EA",
                new BigDecimal("1200.00"), "LKR", Map.of("ram", "32GB"), active);
    }
}
