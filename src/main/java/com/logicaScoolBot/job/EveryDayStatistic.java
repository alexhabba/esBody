package com.logicaScoolBot.job;

import com.logicaScoolBot.repository.QrRepository;
import com.logicaScoolBot.repository.UserRepository;
import com.logicaScoolBot.service.ConsumptionService;
import com.logicaScoolBot.service.ConsumptionServiceImpl;
import com.logicaScoolBot.service.SenderService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.logicaScoolBot.enums.Role.SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class EveryDayStatistic {

    private final UserRepository userRepository;
    private final QrRepository qrRepository;
    private final SenderService senderService;
    private final ConsumptionService consumptionService;


    @Timed("statisticEveryDay")
    @Scheduled(cron = "${cron.job.statisticEveryDay}")
    public void executeJob() {
        LocalDateTime dateTimeMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).atStartOfDay();
        Integer amountSumMonth = qrRepository.getAmountSumToMonth(dateTimeMonth);
        String amountMonth = "Сумма оплат за текущий месяц по СБП " + getFormatNumber(amountSumMonth);

        int amountSumMonthConsumption = consumptionService.getAmountMonth(dateTimeMonth);
        String amountMonthConsumption = "Расход за текущий месяц " + getFormatNumber(amountSumMonthConsumption);

        LocalDateTime dateTimeDay = LocalDate.now().atStartOfDay();
        Integer amountSumDay = qrRepository.getAmountSumToDay(dateTimeDay);
        String amountDay = "Сумма оплат за текущий день по СБП " + getFormatNumber(amountSumDay);

        userRepository.findAllByRole(SUPER_ADMIN).forEach(user -> {
            senderService.send(user.getChatId(), amountMonth);
            senderService.send(user.getChatId(), amountMonthConsumption);
            senderService.send(user.getChatId(), amountDay);
        });
    }

    private String getFormatNumber(int number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        // вот тут устанавливаем разделитель он и так по умолчанию пробел,
        // но в этом примере я решил это сделать явно
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        // указываем сколько символов в группе
        df.setGroupingSize(3);
        return df.format(number);
    }
}
