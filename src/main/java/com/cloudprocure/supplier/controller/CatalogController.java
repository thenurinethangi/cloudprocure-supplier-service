package com.cloudprocure.supplier.controller;

import com.cloudprocure.supplier.dto.CatalogItemRequest;
import com.cloudprocure.supplier.dto.CatalogItemResponse;
import com.cloudprocure.supplier.dto.CreateCatalogItemRequest;
import com.cloudprocure.supplier.dto.PageResponse;
import com.cloudprocure.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.net.URI;

@RestController
@RequestMapping("/api/catalog-items")
public class CatalogController {
    private final SupplierService supplierService;

    public CatalogController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public ResponseEntity<CatalogItemResponse> create(@Valid @RequestBody CreateCatalogItemRequest request) {
        CatalogItemResponse created = supplierService.createCatalogItem(request.supplierId(), request.catalogItem());
        return ResponseEntity.created(URI.create("/api/catalog-items/" + created.id())).body(created);
    }

    @GetMapping
    public PageResponse<CatalogItemResponse> list(@RequestParam(required = false) UUID supplierId,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) Boolean active,
                                                  @RequestParam(required = false) String search,
                                                  @PageableDefault(size = 20, sort = "name",
                                                          direction = Sort.Direction.ASC) Pageable pageable) {
        return supplierService.listCatalog(supplierId, category, active, search, pageable);
    }

    @GetMapping("/{id}")
    public CatalogItemResponse get(@PathVariable UUID id) {
        return supplierService.getCatalogItem(id);
    }

    @PutMapping("/{id}")
    public CatalogItemResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody CatalogItemRequest request) {
        return supplierService.updateCatalogItem(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supplierService.deleteCatalogItem(id);
        return ResponseEntity.noContent().build();
    }
}
