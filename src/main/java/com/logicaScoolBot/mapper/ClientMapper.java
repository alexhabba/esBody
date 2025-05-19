package com.logicaScoolBot.mapper;

import com.logicaScoolBot.dto.kafka.ClientDto;
import com.logicaScoolBot.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {
    ClientDto toDto(Client entity);
}
