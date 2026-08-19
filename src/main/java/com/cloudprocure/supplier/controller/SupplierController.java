package com.cloudprocure.supplier.controller;

import com.cloudprocure.supplier.domain.SupplierStatus;
import com.cloudprocure.supplier.dto.CatalogItemRequest;
import com.cloudprocure.supplier.dto.CatalogItemResponse;
import com.cloudprocure.supplier.dto.CreateSupplierRequest;
import com.cloudprocure.supplier.dto.PageResponse;
import com.cloudprocure.supplier.dto.SupplierResponse;
import com.cloudprocure.supplier.dto.UpdateSupplierRequest;
import com.cloudprocure.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
        SupplierResponse created = supplierService.create(request);
        return ResponseEntity.created(URI.create("/api/suppliers/" + created.id())).body(created);
    }

    @GetMapping
    public PageResponse<SupplierResponse> list(@RequestParam(required = false) SupplierStatus status,
                                               @RequestParam(required = false) String category,
                                               @RequestParam(required = false) String search,
                                               @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
                                               Pageable pageable) {
        return supplierService.list(status, category, search, pageable);
    }

    @GetMapping("/search")
    public PageResponse<SupplierResponse> search(@RequestParam String query,
                                                 @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return supplierService.list(null, null, query, pageable);
    }

    @GetMapping("/{id}")
    public SupplierResponse get(@PathVariable UUID id) {
        return supplierService.get(id);
    }

    @PutMapping("/{id}")
    public SupplierResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateSupplierRequest request) {
        return supplierService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/catalog-items")
    public ResponseEntity<CatalogItemResponse> addCatalogItem(@PathVariable UUID id,
                                                              @Valid @RequestBody CatalogItemRequest request) {
        CatalogItemResponse created = supplierService.createCatalogItem(id, request);
        return ResponseEntity.created(URI.create("/api/catalog-items/" + created.id())).body(created);
    }

    @GetMapping({"/{id}/catalog-items", "/{id}/catalog"})
    public PageResponse<CatalogItemResponse> supplierCatalog(
            @PathVariable UUID id,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return supplierService.listCatalog(id, null, null, null, pageable);
    }
}
