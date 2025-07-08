package com.onclass.capacity.domain.model.spi;

import java.util.List;

public record CapacityTechnologies(
        Long id,
        List<TechnologyItem> technologies
    ) {
}
