package com.onclass.capacity.domain.api;

import com.onclass.capacity.domain.enums.ItemSortList;
import com.onclass.capacity.domain.enums.OrderList;
import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.domain.model.CapacityList;
import com.onclass.capacity.domain.utilities.CustomPage;
import reactor.core.publisher.Mono;

public interface CapacityServicePort {
    Mono<Capacity> registerCapacity(Capacity capacity);
    Mono<CustomPage<CapacityList>> listCapabilities(OrderList order, ItemSortList item, Integer page, Integer size);

}
