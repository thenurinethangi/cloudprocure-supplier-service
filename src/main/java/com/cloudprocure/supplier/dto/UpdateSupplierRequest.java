package com.cloudprocure.supplier.dto;

import com.cloudprocure.supplier.domain.Address;
import com.cloudprocure.supplier.domain.Contact;
import com.cloudprocure.supplier.domain.SupplierStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.List;

public record UpdateSupplierRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 250) String legalName,
        @Size(max = 100) String registrationNumber,
        SupplierStatus status,
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal rating,
        @NotEmpty Set<@NotBlank @Size(max = 100) String> categories,
        List<@Valid Contact> contacts,
        @Valid Address address,
        @Size(max = 2000) String notes
) {
}
