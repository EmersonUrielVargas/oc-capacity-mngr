package com.onclass.capacity.infrastructure.adapters.persistenceadapter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "capabilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityEntity {
    @Id
    private Long id;
    private String name;
    private String description;
}
