package com.es.body.service;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QrService {
    void updateQrStatuses(List<String> qrsIdList, LocalDateTime dateTime);

    Integer getAmountSumToDay(LocalDateTime dateTime);

}
