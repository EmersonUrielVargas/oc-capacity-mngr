package com.onclass.capacity.infrastructure.adapters.technologiesadapter.dto.request;

import java.util.List;

public record TechnologyAssign(Long capacityId, List<Long> technologiesIds) {
}
