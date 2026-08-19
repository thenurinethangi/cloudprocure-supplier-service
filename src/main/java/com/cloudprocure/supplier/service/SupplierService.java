package com.cloudprocure.supplier.service;

import com.cloudprocure.supplier.activity.ActivityEvent;
import com.cloudprocure.supplier.activity.ActivityPublisher;
import com.cloudprocure.supplier.domain.CatalogItem;
import com.cloudprocure.supplier.domain.Supplier;
import com.cloudprocure.supplier.domain.SupplierStatus;
import com.cloudprocure.supplier.dto.CatalogItemRequest;
import com.cloudprocure.supplier.dto.CatalogItemResponse;
import com.cloudprocure.supplier.dto.CreateSupplierRequest;
import com.cloudprocure.supplier.dto.PageResponse;
import com.cloudprocure.supplier.dto.SupplierResponse;
import com.cloudprocure.supplier.dto.UpdateSupplierRequest;
import com.cloudprocure.supplier.exception.BusinessRuleException;
import com.cloudprocure.supplier.exception.ResourceNotFoundException;
import com.cloudprocure.supplier.repository.CatalogItemRepository;
import com.cloudprocure.supplier.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.Map;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final Clock clock;
    private final ActivityPublisher activityPublisher;
    private final ActorProvider actorProvider;

    public SupplierService(SupplierRepository supplierRepository, CatalogItemRepository catalogItemRepository,
                           Clock clock, ActivityPublisher activityPublisher, ActorProvider actorProvider) {
        this.supplierRepository = supplierRepository;
        this.catalogItemRepository = catalogItemRepository;
        this.clock = clock;
        this.activityPublisher = activityPublisher;
        this.actorProvider = actorProvider;
    }

    public SupplierResponse create(CreateSupplierRequest request) {
        String normalizedCode = request.code().trim().toUpperCase();
        if (supplierRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new BusinessRuleException("A supplier with this code already exists");
        }
        Supplier supplier = Supplier.create(UUID.randomUUID(), normalizedCode, request.name(), request.legalName(),
                request.registrationNumber(), request.status(), request.rating(), request.categories(),
                request.contacts(), request.address(), request.notes(), clock.instant());
        Supplier saved = supplierRepository.save(supplier);
        publish(saved, "SUPPLIER_CREATED", "Supplier created");
        return SupplierResponse.from(saved);
    }

    public SupplierResponse get(UUID id) {
        return SupplierResponse.from(findSupplier(id));
    }

    public SupplierResponse getActive(UUID id) {
        Supplier supplier = findSupplier(id);
        if (!supplier.isActive()) {
            throw new BusinessRuleException("Purchase orders require an active supplier");
        }
        return SupplierResponse.from(supplier);
    }

    public PageResponse<SupplierResponse> list(SupplierStatus status, String category, String search,
                                               Pageable pageable) {
        Page<Supplier> suppliers;
        if (search != null && !search.isBlank()) {
            suppliers = supplierRepository
                    .findByNameContainingIgnoreCaseOrLegalNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                            search, search, search, pageable);
        } else if (status != null) {
            suppliers = supplierRepository.findAllByStatus(status, pageable);
        } else if (category != null && !category.isBlank()) {
            suppliers = supplierRepository.findAllByCategoriesContaining(category.trim().toUpperCase(), pageable);
        } else {
            suppliers = supplierRepository.findAll(pageable);
        }
        return PageResponse.from(suppliers.map(SupplierResponse::from));
    }

    public SupplierResponse update(UUID id, UpdateSupplierRequest request) {
        Supplier supplier = findSupplier(id);
        SupplierStatus previousStatus = supplier.getStatus();
        Instant now = clock.instant();
        supplier.update(request.name(), request.legalName(), request.registrationNumber(), request.status(),
                request.rating(), request.categories(), request.contacts(), request.address(), request.notes(), now);
        if (previousStatus == SupplierStatus.ACTIVE && !supplier.isActive()) {
            catalogItemRepository.deactivateAllBySupplierId(id, now);
        }
        Supplier saved = supplierRepository.save(supplier);
        publish(saved, "SUPPLIER_STATUS_CHANGED", "Supplier updated");
        return SupplierResponse.from(saved);
    }

    public void delete(UUID id) {
        Supplier supplier = findSupplier(id);
        Instant now = clock.instant();
        supplier.deactivate(now);
        supplierRepository.save(supplier);
        catalogItemRepository.deactivateAllBySupplierId(id, now);
        publish(supplier, "SUPPLIER_STATUS_CHANGED", "Supplier deactivated");
    }

    public CatalogItemResponse createCatalogItem(UUID supplierId, CatalogItemRequest request) {
        Supplier supplier = findSupplier(supplierId);
        requireActiveIfNeeded(supplier, request.active());
        String normalizedSku = request.sku().trim().toUpperCase();
        if (catalogItemRepository.existsBySupplierIdAndSkuIgnoreCase(supplierId, normalizedSku)) {
            throw new BusinessRuleException("This supplier already has a catalog item with that SKU");
        }
        CatalogItem item = CatalogItem.create(UUID.randomUUID(), supplierId, normalizedSku, request.name(),
                request.description(), request.category(), request.unit(), request.price(), request.currency(),
                request.attributes(), request.active(), clock.instant());
        CatalogItem saved = catalogItemRepository.save(item);
        publish(supplier, "CATALOG_ITEM_CREATED", "Catalog item created");
        return CatalogItemResponse.from(saved);
    }

    public CatalogItemResponse getCatalogItem(UUID id) {
        return CatalogItemResponse.from(findCatalogItem(id));
    }

    public CatalogItemResponse updateCatalogItem(UUID id, CatalogItemRequest request) {
        CatalogItem item = findCatalogItem(id);
        Supplier supplier = findSupplier(item.getSupplierId());
        requireActiveIfNeeded(supplier, request.active());
        item.update(request.name(), request.description(), request.category(), request.unit(), request.price(),
                request.currency(), request.attributes(), request.active(), clock.instant());
        return CatalogItemResponse.from(catalogItemRepository.save(item));
    }

    public void deleteCatalogItem(UUID id) {
        catalogItemRepository.delete(findCatalogItem(id));
    }

    public PageResponse<CatalogItemResponse> listCatalog(UUID supplierId, String category, Boolean active,
                                                         String search, Pageable pageable) {
        Page<CatalogItem> items;
        if (search != null && !search.isBlank()) {
            items = catalogItemRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrSkuContainingIgnoreCase(
                            search, search, search, pageable);
        } else if (supplierId != null) {
            items = catalogItemRepository.findAllBySupplierId(supplierId, pageable);
        } else if (category != null && !category.isBlank()) {
            items = catalogItemRepository.findAllByCategoryIgnoreCase(category, pageable);
        } else if (active != null) {
            items = catalogItemRepository.findAllByActive(active, pageable);
        } else {
            items = catalogItemRepository.findAll(pageable);
        }
        return PageResponse.from(items.map(CatalogItemResponse::from));
    }

    private void requireActiveIfNeeded(Supplier supplier, boolean catalogActive) {
        if (catalogActive && !supplier.isActive()) {
            throw new BusinessRuleException("An active catalog item requires an active supplier");
        }
    }

    private Supplier findSupplier(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
    }

    private CatalogItem findCatalogItem(UUID id) {
        return catalogItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog item not found"));
    }

    private void publish(Supplier supplier, String eventType, String summary) {
        activityPublisher.publish(new ActivityEvent(UUID.randomUUID(), "supplier-service", eventType, "SUPPLIER",
                supplier.getId().toString(), actorProvider.currentActorEmail(), summary,
                Map.of("supplierCode", supplier.getCode(), "status", supplier.getStatus().name()), clock.instant()));
    }
}
