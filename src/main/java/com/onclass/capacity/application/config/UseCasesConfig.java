package com.onclass.capacity.application.config;

import com.onclass.capacity.domain.api.CapacityServicePort;
import com.onclass.capacity.domain.spi.CapacityPersistencePort;
import com.onclass.capacity.domain.spi.TechnologiesGateway;
import com.onclass.capacity.domain.usecase.CapacityUseCase;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.CapacityPersistenceAdapter;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.mapper.CapacityEntityMapper;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.repository.CapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final CapacityRepository capacityRepository;
        private final CapacityEntityMapper capacityEntityMapper;

        @Bean
        public CapacityPersistencePort capacityPersistencePort() {
                return new CapacityPersistenceAdapter(capacityRepository, capacityEntityMapper);
        }

        @Bean
        public CapacityServicePort capacityServicePort(CapacityPersistencePort capacityPersistencePort, TechnologiesGateway technologiesGateway){
                return new CapacityUseCase(capacityPersistencePort, technologiesGateway);
        }
}
