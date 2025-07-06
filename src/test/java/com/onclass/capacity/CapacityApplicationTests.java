package com.onclass.capacity;

import com.onclass.capacity.domain.spi.CapacityPersistencePort;
import com.onclass.capacity.domain.usecase.CapacityUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = CapacityApplication.class)
class CapacityApplicationTests {

	@MockitoBean
	private CapacityPersistencePort capacityPersistencePort;

	@Autowired
	private CapacityUseCase technologyUseCase;

	@Test
	void contextLoads() {
	}

}
