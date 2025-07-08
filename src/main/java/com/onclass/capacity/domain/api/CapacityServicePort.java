package com.onclass.capacity.domain.api;

import com.onclass.capacity.domain.enums.ItemSortList;
import com.onclass.capacity.domain.enums.OrderList;
import com.onclass.capacity.domain.model.spi.CapabilitiesPerBootcamp;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.spi.CapacityList;
import com.onclass.capacity.domain.utilities.CustomPage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityServicePort {
    Mono<Capacity> registerCapacity(Capacity capacity);
    Mono<CustomPage<CapacityList>> listCapabilities(OrderList order, ItemSortList item, Integer page, Integer size);
    Mono<Void> assignCapabilitiesToBootcamp(Long bootcampId, List<Long> capabilitiesIds);
    Flux<CapabilitiesPerBootcamp> getCapabilitiesByBootcampsIds(List<Long> bootcampIds);
    Mono<CustomPage<CapabilitiesPerBootcamp>> getSortCapabilitiesByBootcamps(OrderList order, Integer size, Integer page);
    Mono<Void> deleteCapabilitiesByBootcampId(Long bootcampId);

}
