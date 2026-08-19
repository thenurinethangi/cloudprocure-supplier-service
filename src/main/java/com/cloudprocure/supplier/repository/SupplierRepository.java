package com.cloudprocure.supplier.repository;

import com.cloudprocure.supplier.domain.Supplier;
import com.cloudprocure.supplier.domain.SupplierStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends MongoRepository<Supplier, UUID> {
    boolean existsByCodeIgnoreCase(String code);
    Optional<Supplier> findByCodeIgnoreCase(String code);
    Page<Supplier> findAllByStatus(SupplierStatus status, Pageable pageable);
    Page<Supplier> findAllByCategoriesContaining(String category, Pageable pageable);
    Page<Supplier> findByNameContainingIgnoreCaseOrLegalNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
            String name, String legalName, String code, Pageable pageable);
}
