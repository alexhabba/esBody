package com.logicaScoolBot.mapper;

import com.logicaScoolBot.dto.kafka.AdministratorWorkDayDto;
import com.logicaScoolBot.dto.kafka.QrDto;
import com.logicaScoolBot.entity.AdministratorWorkDay;
import com.logicaScoolBot.entity.Qr;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdministratorWorkDayMapper {
    AdministratorWorkDayDto toDto(AdministratorWorkDay entity);
}
