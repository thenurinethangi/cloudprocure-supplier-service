package com.cloudprocure.supplier.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Contact(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 100) String jobTitle,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 50) String phone,
        boolean primary) {
}
