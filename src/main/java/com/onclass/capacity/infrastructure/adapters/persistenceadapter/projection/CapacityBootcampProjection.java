package com.onclass.capacity.infrastructure.adapters.persistenceadapter.projection;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CapacityBootcampProjection {
	private Long bootcampId;
	private Long capacityId;
	private String capacityName;
}
