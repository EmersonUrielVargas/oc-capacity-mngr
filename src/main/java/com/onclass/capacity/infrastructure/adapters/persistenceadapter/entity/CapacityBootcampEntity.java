package com.onclass.capacity.infrastructure.adapters.persistenceadapter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacity_bootcamp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityBootcampEntity {
	@Id
    private Long id;

    @Column("id_capacity")
    private Long capacityId;

    @Column("id_bootcamp")
    private Long bootcampId;
}
