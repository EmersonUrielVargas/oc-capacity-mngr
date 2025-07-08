package com.onclass.capacity.infrastructure.entrypoints.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class AssignCapabilitiesDTO {
    private Long bootcampId;
    private List<Long> capabilitiesIds;
}
