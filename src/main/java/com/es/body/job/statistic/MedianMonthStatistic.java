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
import static com.es.body.enums.StatisticType.MEDIAN_MONTH;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class MedianMonthStatistic {

    private final UserRepository userRepository;
    private final SenderService senderService;
    private final ConsumptionService consumptionService;
    private final StatisticService statisticService;

    @Scheduled(cron = "${cron.job.statisticMedianMonth}")

//    @Scheduled(fixedDelay = 1000)
    public void executeJob() {
        LocalDateTime dateTimeMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).atStartOfDay();
        List<Consumption> todayConsumptions = consumptionService.getConsumptionByDateTime(dateTimeMonth);
        String infoStatistic = statisticService.getInfoStatistic(todayConsumptions, MEDIAN_MONTH);
        if (nonNull(infoStatistic)) {
            userRepository.findAllByRoles(List.of(ADMIN_TEST, SUPER_ADMIN, ACCOUNTANT)).forEach(user -> {
                senderService.send(user.getChatId(), infoStatistic);
            });
        }
    }

}
