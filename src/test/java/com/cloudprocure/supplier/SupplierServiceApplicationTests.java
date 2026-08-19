package com.cloudprocure.supplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cloudprocure.supplier.repository.CatalogItemRepository;
import com.cloudprocure.supplier.repository.SupplierRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"debug=false",
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false",
		"spring.autoconfigure.exclude=org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration,"
				+ "org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration",
		"management.endpoints.web.exposure.include=health",
		"management.endpoint.health.probes.enabled=true"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SupplierServiceApplicationTests {
	@Autowired MockMvc mockMvc;
	@MockitoBean SupplierRepository supplierRepository;
	@MockitoBean CatalogItemRepository catalogItemRepository;

	@Test
	void exposesHealthForPlatformChecks() throws Exception {
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"));
	}

}
