package com.es.body.service;

import java.time.LocalDateTime;
import java.util.List;

public interface QrService {
    void updateQrStatuses(List<String> qrsIdList, LocalDateTime dateTime);
}
