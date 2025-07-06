package com.onclass.capacity.infrastructure.adapters.technologiesadapter.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BulkheadConfiguration {

    private final BulkheadRegistry bulkheadRegistry;

    public BulkheadConfiguration(BulkheadRegistry bulkheadRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Bean
    public Bulkhead technologyMngrBulkhead() {
        return bulkheadRegistry.bulkhead("technologyMngrBulkhead");
    }
}
