package com.onclass.capacity.infrastructure.entrypoints.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BasicCapacityDTO {
    private Long id;
    private String name;
    private String description;
}
