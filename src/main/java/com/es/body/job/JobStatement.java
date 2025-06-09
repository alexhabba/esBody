package com.es.body.job;

import com.es.body.enums.OrgType;
import com.es.body.repository.QrRepository;
import com.es.body.repository.UserRepository;
import com.es.body.service.ConsumptionService;
import com.es.body.service.SenderService;
import com.es.body.statement.service.CommonStatementService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.es.body.enums.Role.SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class JobStatement {

    private final CommonStatementService commonStatementService;


    @Scheduled(cron = "${cron.job.statement}")
//    @Scheduled(fixedDelay = 100)
    public void executeJob() {
        commonStatementService.getStatement(OrgType.DESERT);
        commonStatementService.getStatement(OrgType.DELIVERY);
    }

}
