package com.onclass.capacity.infrastructure.adapters.persistenceadapter.projection;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BootcampCountProjection {
	private Long bootcampId;
	private int capabilitiesCount;
}
