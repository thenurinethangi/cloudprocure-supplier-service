package com.cloudprocure.supplier.repository;

import com.cloudprocure.supplier.domain.CatalogItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.time.Instant;
import java.util.UUID;

public interface CatalogItemRepository extends MongoRepository<CatalogItem, UUID> {
    boolean existsBySupplierIdAndSkuIgnoreCase(UUID supplierId, String sku);
    Page<CatalogItem> findAllBySupplierId(UUID supplierId, Pageable pageable);
    Page<CatalogItem> findAllByCategoryIgnoreCase(String category, Pageable pageable);
    Page<CatalogItem> findAllByActive(boolean active, Pageable pageable);
    Page<CatalogItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrSkuContainingIgnoreCase(
            String name, String description, String sku, Pageable pageable);

    @Query("{'supplierId': ?0, 'active': true}")
    @Update("{'$set': {'active': false, 'updatedAt': ?1}}")
    long deactivateAllBySupplierId(UUID supplierId, Instant updatedAt);
}
