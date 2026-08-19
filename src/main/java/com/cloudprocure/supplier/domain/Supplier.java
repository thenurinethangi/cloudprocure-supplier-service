package com.cloudprocure.supplier.domain;

import com.cloudprocure.supplier.exception.BusinessRuleException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document("suppliers")
public class Supplier {
    @Id private UUID id;
    @Indexed(unique = true) private String code;
    @TextIndexed private String name;
    @TextIndexed private String legalName;
    @Indexed(sparse = true) private String registrationNumber;
    @Indexed private SupplierStatus status;
    private BigDecimal rating;
    @Indexed private Set<String> categories;
    private List<Contact> contacts;
    private Address address;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    protected Supplier() {
    }

    public static Supplier create(UUID id, String code, String name, String legalName, String registrationNumber,
                                  SupplierStatus status, BigDecimal rating, Set<String> categories,
                                  List<Contact> contacts, Address address, String notes, Instant now) {
        Supplier supplier = new Supplier();
        supplier.id = id;
        supplier.code = required(code, "Supplier code").toUpperCase();
        supplier.createdAt = now;
        supplier.update(name, legalName, registrationNumber, status, rating, categories, contacts, address, notes, now);
        return supplier;
    }

    public void update(String name, String legalName, String registrationNumber, SupplierStatus status,
                       BigDecimal rating, Set<String> categories, List<Contact> contacts, Address address,
                       String notes, Instant now) {
        this.name = required(name, "Supplier name");
        this.legalName = optional(legalName);
        this.registrationNumber = optional(registrationNumber);
        this.status = status == null ? SupplierStatus.ACTIVE : status;
        if (rating != null && (rating.signum() < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0)) {
            throw new BusinessRuleException("Supplier rating must be between zero and five");
        }
        this.rating = rating;
        this.categories = normalizedCategories(categories);
        this.contacts = contacts == null ? List.of() : List.copyOf(contacts);
        this.address = address;
        this.notes = optional(notes);
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.status = SupplierStatus.INACTIVE;
        this.updatedAt = now;
    }

    public boolean isActive() {
        return status == SupplierStatus.ACTIVE;
    }

    private static Set<String> normalizedCategories(Set<String> categories) {
        Set<String> normalized = new LinkedHashSet<>();
        if (categories != null) {
            categories.stream().filter(value -> value != null && !value.isBlank())
                    .map(value -> value.trim().toUpperCase()).forEach(normalized::add);
        }
        if (normalized.isEmpty()) {
            throw new BusinessRuleException("Supplier needs at least one category");
        }
        return Set.copyOf(normalized);
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(field + " is required");
        }
        return value.trim();
    }

    private static String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public SupplierStatus getStatus() { return status; }
    public BigDecimal getRating() { return rating; }
    public Set<String> getCategories() { return categories; }
    public List<Contact> getContacts() { return contacts; }
    public Address getAddress() { return address; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
