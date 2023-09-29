package com.logicaScoolBot.mapper;

import com.logicaScoolBot.dto.kafka.QrDto;
import com.logicaScoolBot.entity.Qr;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QrMapper {

    @Mapping(target = "studentId", source = "entity.student.id")
    QrDto toDto(Qr entity);
}
