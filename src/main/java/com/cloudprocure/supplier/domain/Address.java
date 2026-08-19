package com.cloudprocure.supplier.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(
        @NotBlank @Size(max = 250) String line1,
        @Size(max = 250) String line2,
        @NotBlank @Size(max = 120) String city,
        @Size(max = 120) String stateOrProvince,
        @Size(max = 30) String postalCode,
        @NotBlank @Size(max = 100) String country) {
}
