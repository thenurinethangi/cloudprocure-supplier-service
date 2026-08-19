package com.cloudprocure.supplier.controller;

import com.cloudprocure.supplier.dto.SupplierResponse;
import com.cloudprocure.supplier.service.SupplierService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/suppliers")
public class InternalSupplierController {
    private final SupplierService supplierService;

    public InternalSupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/{id}/active")
    public SupplierResponse active(@PathVariable UUID id) {
        return supplierService.getActive(id);
    }
}
