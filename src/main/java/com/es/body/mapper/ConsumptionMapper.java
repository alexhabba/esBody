package com.es.body.mapper;

import com.es.body.dto.kafka.ConsumptionDto;
import com.es.body.entity.Consumption;
import com.es.body.statement.dto.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsumptionMapper {
    ConsumptionDto toDto(Consumption entity);

    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "name", source = "creditorParty.name")
    @Mapping(target = "creditDebit", source = "creditDebitIndicator")
    Consumption toEntity(TransactionDto dto);
}
