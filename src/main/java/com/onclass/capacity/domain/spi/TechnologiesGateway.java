package com.onclass.capacity.domain.spi;

import com.onclass.capacity.domain.model.spi.CapacityTechnologies;
import com.onclass.capacity.domain.utilities.CustomPage;
import com.onclass.capacity.infrastructure.entrypoints.util.ResponseDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologiesGateway {
    Mono<ResponseDTO> assignTechnologiesToCapacity(Long capacityId, List<Long> technologiesIds);
    Mono<List<CapacityTechnologies>> getTechnologiesByCapabilitiesIds(List<Long> capabilitiesIds);
    Mono<CustomPage<CapacityTechnologies>> getSortTechnologiesByCapabilities(String order, Integer size, Integer page);
    Mono<Void> deleteTechnologiesByCapabilitiesIds(List<Long> capabilitiesIds);
}
