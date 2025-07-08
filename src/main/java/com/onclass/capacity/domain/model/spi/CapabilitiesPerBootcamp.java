package com.onclass.capacity.domain.model.spi;

import java.util.List;

public record CapabilitiesPerBootcamp(
        Long id,
        List<CapacityItem> capabilities
    ) {
}
