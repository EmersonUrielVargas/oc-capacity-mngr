package com.onclass.capacity.domain.model;

import java.util.List;

public record CapacityTechnologies(
        Long id,
        List<TechnologyItem> technologies
    ) {
}
