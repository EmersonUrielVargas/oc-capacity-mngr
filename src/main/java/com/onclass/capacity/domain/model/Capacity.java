package com.onclass.capacity.domain.model;

import java.util.List;

public record Capacity(
        Long id,
        String name,
        String description,
        List<Long> technologies
    ) {
}
