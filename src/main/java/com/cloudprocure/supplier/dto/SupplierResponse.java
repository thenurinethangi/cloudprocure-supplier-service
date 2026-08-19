package com.cloudprocure.supplier.dto;

import com.cloudprocure.supplier.domain.Address;
import com.cloudprocure.supplier.domain.Contact;
import com.cloudprocure.supplier.domain.Supplier;
import com.cloudprocure.supplier.domain.SupplierStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.List;
import java.util.UUID;

public record SupplierResponse(UUID id, String code, String name, String legalName, String registrationNumber,
                               SupplierStatus status, BigDecimal rating, Set<String> categories,
                               List<Contact> contacts, Address address, String notes, Instant createdAt, Instant updatedAt) {
    public static SupplierResponse from(Supplier supplier) {
        return new SupplierResponse(supplier.getId(), supplier.getCode(), supplier.getName(),
                supplier.getLegalName(), supplier.getRegistrationNumber(), supplier.getStatus(), supplier.getRating(),
                supplier.getCategories(), supplier.getContacts(), supplier.getAddress(), supplier.getNotes(),
                supplier.getCreatedAt(), supplier.getUpdatedAt());
    }
}
