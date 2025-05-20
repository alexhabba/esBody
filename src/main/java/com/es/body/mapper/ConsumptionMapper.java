package com.es.body.mapper;

import com.es.body.dto.kafka.ConsumptionDto;
import com.es.body.entity.Consumption;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsumptionMapper {
    ConsumptionDto toDto(Consumption entity);
}
