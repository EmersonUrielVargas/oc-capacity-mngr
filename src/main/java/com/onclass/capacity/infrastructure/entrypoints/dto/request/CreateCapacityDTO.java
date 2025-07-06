package com.onclass.capacity.infrastructure.entrypoints.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CreateCapacityDTO {
    private String name;
    private String description;
    private List<Long> technologies;
}
