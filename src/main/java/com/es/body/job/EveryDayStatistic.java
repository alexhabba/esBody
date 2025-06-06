package com.es.body.job;

import com.es.body.entity.Consumption;
import com.es.body.enums.OrgType;
import com.es.body.repository.QrRepository;
import com.es.body.repository.UserRepository;
import com.es.body.service.ConsumptionService;
import com.es.body.service.SenderService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.es.body.enums.CreditDebitIndicator.Credit;
import static com.es.body.enums.CreditDebitIndicator.Debit;
import static com.es.body.enums.OrgType.DELIVERY;
import static com.es.body.enums.OrgType.DESERT;
import static com.es.body.enums.Role.*;

@Service
@RequiredArgsConstructor
public class EveryDayStatistic {

    private final UserRepository userRepository;
    private final QrRepository qrRepository;
    private final SenderService senderService;
    private final ConsumptionService consumptionService;

    @Timed("statisticEveryDay")
    @Scheduled(cron = "${cron.job.statisticEveryDay}")
//    @Scheduled(fixedDelay = 1000)
    public void executeJob() {
//        LocalDateTime dateTimeMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).atStartOfDay();
//        Integer amountSumMonth = qrRepository.getAmountSumToMonth(dateTimeMonth);
//        String amountMonth = "Сумма оплат за текущий месяц по СБП " + getFormatNumber(amountSumMonth);
//
//        int amountSumMonthConsumption = consumptionService.getAmountMonth(dateTimeMonth);
////        String amountMonthConsumption = "Расход за текущий месяц " + getFormatNumber(amountSumMonthConsumption);
//
        LocalDateTime dateTimeDay = LocalDate.now().atStartOfDay();
        List<Consumption> todayConsumptions = consumptionService.getConsumptionToday(dateTimeDay);
        String s = everyDayStatement(todayConsumptions);
        userRepository.findAllByRoles(List.of(ADMIN_TEST, SUPER_ADMIN, ACCOUNTANT)).forEach(user -> {
            senderService.send(user.getChatId(), everyDayStatement(todayConsumptions));
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

    public String everyDayStatement(List<Consumption> consumptions) {
        BigDecimal debitDeliverySum = BigDecimal.ZERO;
        BigDecimal debitDesertSum = BigDecimal.ZERO;
        BigDecimal creditDeliverySum = BigDecimal.ZERO;
        BigDecimal creditDesertSum = BigDecimal.ZERO;

        Map<OrgType, List<Consumption>> orgTypeConsumptions = consumptions.stream()
                .collect(Collectors.groupingBy(Consumption::getOrgType));

        List<Consumption> deliveryConsumptions = orgTypeConsumptions.get(DELIVERY);
        if (!deliveryConsumptions.isEmpty()) {
            creditDeliverySum = deliveryConsumptions.stream()
                    .filter(c -> Credit == c.getCreditDebit())
                    .map(Consumption::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            debitDeliverySum = deliveryConsumptions.stream()
                    .filter(c -> Debit == c.getCreditDebit())
                    .map(Consumption::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        List<Consumption> desertConsumptions = orgTypeConsumptions.get(DESERT);
        if (!desertConsumptions.isEmpty()) {
            creditDesertSum = desertConsumptions.stream()
                    .filter(c -> Credit == c.getCreditDebit())
                    .map(Consumption::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            debitDesertSum = desertConsumptions.stream()
                    .filter(c -> Debit == c.getCreditDebit())
                    .map(Consumption::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        String everyStatement =
                "Ежедневный отчет.\n" +
                        "\uD83D\uDFE2 Приход\n" +
                        "«Рационы» " + creditDeliverySum.intValue() + "\n" +
                        "«Десерты» " + creditDesertSum.intValue() + "\n" +
                        "\uD83D\uDD34 Расход\n" +
                        "«Рационы» " + debitDeliverySum.intValue() + "\n" +
                        "«Десерты» " + debitDesertSum.intValue() + "\n";

        return everyStatement;
    }

//    Расход 606.65 "Десерты"
//    Покупка товара(Терминал:PYATEROCHKA,PASS SADOVYY 2-Y 2,Penza,RU,дата операции:03/06/2025 10:08(МСК),на сумму:606.65 RUB,карта 2204********1052) ООО "Банк Точка"

//    Ежедневно
//    Сумма оплат за текущий день 20000
//            «Рационы» 19 730
//            «Десерты» 19 730
//
//    Расход за текущий день 30000
//            "«Рационы» 10000
//            "«Десерты» 1000
//
//
//    Каждое 15 число месяца
//    Сумма оплат за текущий месяц 20000
//            «Доставка» 19 730
//            «Десерты» 19 730
//
//    Расход за текущий месяц 30000
//            «Доставка» 10000
//            «Десерты» 1000
//
//    Прибыль за текущий месяц 30000
//            «Доставка» 10000
//            «Десерты» 1000
//
//
//    Каждый последний день месяца
//    Сумма оплат за текущий месяц 20000
//            «Доставка» 19 730
//            «Десерты» 19 730
//
//    Расход за текущий месяц 30000
//            «Доставка» 10000
//            «Десерты» 1000
//
//    Прибыль за текущий месяц 30000
//            «Доставка» 10000
//            «Десерты» 1000
}
