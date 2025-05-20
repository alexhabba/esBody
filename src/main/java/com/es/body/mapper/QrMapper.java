package com.es.body.mapper;

import com.es.body.entity.Qr;
import com.es.body.dto.kafka.QrDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QrMapper {

    @Mapping(target = "clientId", source = "entity.client.id")
    QrDto toDto(Qr entity);
}
