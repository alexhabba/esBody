package com.logicaScoolBot.mapper;

import com.logicaScoolBot.dto.kafka.StudentDto;
import com.logicaScoolBot.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {
    StudentDto toDto(Student entity);
}
