package com.cloudprocure.supplier.config;

import com.cloudprocure.supplier.domain.Address;
import com.cloudprocure.supplier.domain.CatalogItem;
import com.cloudprocure.supplier.domain.Contact;
import com.cloudprocure.supplier.domain.Supplier;
import com.cloudprocure.supplier.domain.SupplierStatus;
import com.cloudprocure.supplier.repository.CatalogItemRepository;
import com.cloudprocure.supplier.repository.SupplierRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@Profile("local")
public class LocalSupplierSeeder implements ApplicationRunner {
    private final SupplierRepository suppliers;
    private final CatalogItemRepository catalog;
    private final Clock clock;

    public LocalSupplierSeeder(SupplierRepository suppliers, CatalogItemRepository catalog, Clock clock) {
        this.suppliers = suppliers;
        this.catalog = catalog;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedSupplier("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa", "TECH-001", "TechSource Lanka",
                Set.of("IT_EQUIPMENT", "SOFTWARE"), "techsource@example.test");
        seedSupplier("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb", "OFFICE-001", "OfficePro Supplies",
                Set.of("OFFICE_SUPPLIES", "FURNITURE"), "officepro@example.test");
        seedSupplier("cccccccc-cccc-4ccc-8ccc-cccccccccccc", "IND-001", "Global Industrial Solutions",
                Set.of("INDUSTRIAL", "SAFETY"), "globalindustrial@example.test");

        seedCatalog("dddddddd-dddd-4ddd-8ddd-dddddddddddd", "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa",
                "LAP-001", "Business Laptop", "IT_EQUIPMENT", "EA", "425000.00",
                Map.of("ram", "16GB", "cpu", "Intel Core i7", "storage", "512GB SSD"));
        seedCatalog("eeeeeeee-eeee-4eee-8eee-eeeeeeeeeeee", "bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb",
                "CHR-001", "Ergonomic Office Chair", "FURNITURE", "EA", "85000.00",
                Map.of("material", "Mesh", "color", "Black", "maximumWeightKg", 120));
    }

    private void seedSupplier(String id, String code, String name, Set<String> categories, String email) {
        if (suppliers.findByCodeIgnoreCase(code).isPresent()) return;
        Instant now = clock.instant();
        suppliers.save(Supplier.create(UUID.fromString(id), code, name, name + " (Pvt) Ltd", null,
                SupplierStatus.ACTIVE, new BigDecimal("4.5"), categories,
                List.of(new Contact("Supplier Account Manager", "Account Manager", email, "+94 11 555 0100", true)),
                new Address("100 Galle Road", null, "Colombo", "Western", "00300", "Sri Lanka"),
                "Preferred supplier for local workflow verification", now));
    }

    private void seedCatalog(String id, String supplierId, String sku, String name, String category,
                             String unit, String price, Map<String, Object> attributes) {
        UUID owner = UUID.fromString(supplierId);
        if (catalog.existsBySupplierIdAndSkuIgnoreCase(owner, sku)) return;
        catalog.save(CatalogItem.create(UUID.fromString(id), owner, sku, name, null, category, unit,
                new BigDecimal(price), "LKR", attributes, true, clock.instant()));
    }
}
