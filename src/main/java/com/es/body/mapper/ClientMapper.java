package com.es.body.mapper;

import com.es.body.entity.Client;
import com.es.body.dto.kafka.ClientDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {
    ClientDto toDto(Client entity);
}
