package com.logicaScoolBot.mapper;

import com.logicaScoolBot.dto.kafka.ConsumptionDto;
import com.logicaScoolBot.entity.Consumption;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsumptionMapper {
    ConsumptionDto toDto(Consumption entity);
}
