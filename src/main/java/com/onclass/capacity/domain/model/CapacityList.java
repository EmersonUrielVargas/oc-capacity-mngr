package com.onclass.capacity.domain.model;

import java.time.LocalDate;
import java.util.List;

public record CapacityList(
        Long id,
        String name,
        String description,
        List<TechnologyItem> technologies
    ) {
}
