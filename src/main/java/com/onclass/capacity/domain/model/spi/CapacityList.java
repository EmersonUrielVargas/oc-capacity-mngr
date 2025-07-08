package com.onclass.capacity.domain.model.spi;

import java.util.List;

public record CapacityList(
        Long id,
        String name,
        List<TechnologyItem> technologies
    ) {
}
