package com.onclass.capacity.domain.spi;

import com.onclass.capacity.domain.model.CapacityTechnologies;
import com.onclass.capacity.domain.utilities.CustomPage;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologiesGateway {
    Mono<Void> assignTechnologiesToCapacity(Long capacityId, List<Long> technologiesIds);
    Mono<List<CapacityTechnologies>> getTechnologiesByCapabilitiesIds(List<Long> capabilitiesIds);
    Mono<CustomPage<CapacityTechnologies>> getSortTechnologiesByCapabilities(String order, Integer size, Integer page);
}
