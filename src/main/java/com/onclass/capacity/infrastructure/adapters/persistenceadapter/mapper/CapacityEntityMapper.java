package com.onclass.capacity.infrastructure.adapters.persistenceadapter.mapper;

import com.onclass.capacity.domain.model.Capacity;
import com.onclass.capacity.infrastructure.adapters.persistenceadapter.entity.CapacityEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface CapacityEntityMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Capacity toModel(CapacityEntity entity);

    @InheritInverseConfiguration
    CapacityEntity toEntity(Capacity capacity);
}
