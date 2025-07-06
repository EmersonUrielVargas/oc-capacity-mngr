package com.onclass.capacity.infrastructure.adapters.technologiesadapter.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("technology-mngr")
public class TechnologyMngrProperties {
    private String baseUrl;
    private String timeout;
}
