package com.onclass.capacity.infrastructure.entrypoints.mapper;

import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.infrastructure.entrypoints.dto.request.CreateCapacityDTO;
import com.onclass.capacity.infrastructure.entrypoints.dto.response.BasicCapacityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface CapacityMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    BasicCapacityDTO toBasicCapacityDTO(Capacity capacity);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "technologies", target = "technologies")
    Capacity toCapacity(CreateCapacityDTO createCapacityDTO);
}
