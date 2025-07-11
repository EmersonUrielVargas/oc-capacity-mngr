package com.onclass.capacity.infrastructure.adapters.technologiesadapter.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfiguration {

    private final RetryRegistry retryRegistry;

    public RetryConfiguration(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    @Bean
    public Retry retryPolicy() {
        return retryRegistry.retry("technologyMngrRetry");
    }

}
