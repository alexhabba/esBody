package com.es.body.job.statistic;

import com.es.body.entity.Consumption;
import com.es.body.repository.UserRepository;
import com.es.body.service.ConsumptionService;
import com.es.body.service.SenderService;
import com.es.body.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.es.body.enums.Role.*;
import static com.es.body.enums.StatisticType.DAY;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class EveryDayStatistic {

    private final UserRepository userRepository;
    private final SenderService senderService;
    private final ConsumptionService consumptionService;
    private final StatisticService statisticService;

    @Scheduled(cron = "${cron.job.statisticDay}")
//    @Scheduled(fixedDelay = 1000)
    public void executeJob() {
        LocalDateTime dateTimeDay = LocalDate.now().atStartOfDay();
        List<Consumption> todayConsumptions = consumptionService.getConsumptionByDateTime(dateTimeDay);
        String infoStatistic = statisticService.getInfoStatistic(todayConsumptions, DAY);
        if (nonNull(infoStatistic)) {
            userRepository.findAllByRoles(List.of(ADMIN_TEST, SUPER_ADMIN, ACCOUNTANT)).forEach(user -> {
                senderService.send(user.getChatId(), infoStatistic);
            });
        }
    }
}
