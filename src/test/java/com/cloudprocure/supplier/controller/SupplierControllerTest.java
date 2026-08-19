package com.cloudprocure.supplier.controller;

import com.cloudprocure.supplier.exception.BusinessRuleException;
import com.cloudprocure.supplier.exception.GlobalExceptionHandler;
import com.cloudprocure.supplier.service.SupplierService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SupplierControllerTest {
    private SupplierService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(SupplierService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new SupplierController(service), new InternalSupplierController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new SpringValidatorAdapter(
                        Validation.buildDefaultValidatorFactory().getValidator()))
                .build();
    }

    @Test
    void createRejectsBlankCode() throws Exception {
        String body = """
                {
                  "code": " ",
                  "name": "TechSource",
                  "status": "ACTIVE",
                  "categories": ["IT_EQUIPMENT"]
                }
                """;

        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.code").exists());
    }

    @Test
    void internalLookupRejectsInactiveSupplier() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getActive(id)).thenThrow(new BusinessRuleException("Purchase orders require an active supplier"));

        mockMvc.perform(get("/internal/suppliers/{id}/active", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Purchase orders require an active supplier"));
    }
}
