package com.onclass.capacity.domain.model.spi;

import java.util.List;

public record CapabilitiesBasicPerBootcamp(
        Long id,
        List<CapacityBasicItem> capabilities
    ) {
}
