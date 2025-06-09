package com.es.body.service;

import com.es.body.entity.Consumption;
import com.es.body.enums.OrgType;
import com.es.body.enums.StatisticType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.es.body.enums.CreditDebitIndicator.Credit;
import static com.es.body.enums.CreditDebitIndicator.Debit;
import static com.es.body.enums.OrgType.DELIVERY;
import static com.es.body.enums.OrgType.DESERT;
import static com.es.body.enums.StatisticType.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private static final Map<StatisticType, String> MAP_STATISTIC_TYPE_DESCRIPTION =
            Map.of(
                    DAY, "Ежедневный отчет.\n",
                    MEDIAN_MONTH, "Отчет за пол месяца.\n",
                    MONTH, "Отчет за месяц.\n"
            );

    public String getInfoStatistic(List<Consumption> consumptions, StatisticType statisticType) {
        if (isNull(consumptions) || consumptions.isEmpty()) {
            return null;
        }
        BigDecimal debitDeliverySum = BigDecimal.ZERO;
        BigDecimal debitDesertSum = BigDecimal.ZERO;
        BigDecimal creditDeliverySum = BigDecimal.ZERO;
        BigDecimal creditDesertSum = BigDecimal.ZERO;

        Map<OrgType, List<Consumption>> orgTypeConsumptions = consumptions.stream()
                .collect(Collectors.groupingBy(Consumption::getOrgType));

        List<Consumption> deliveryConsumptions = orgTypeConsumptions.get(DELIVERY);
        if (nonNull(deliveryConsumptions) && !deliveryConsumptions.isEmpty()) {
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
        if (nonNull(desertConsumptions) && !desertConsumptions.isEmpty()) {
            creditDesertSum = desertConsumptions.stream()
                    .filter(c -> Credit == c.getCreditDebit())
                    .map(Consumption::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            debitDesertSum = desertConsumptions.stream()
                    .filter(c -> Debit == c.getCreditDebit())
                    .map(Consumption::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        String descriptinMain = MAP_STATISTIC_TYPE_DESCRIPTION.get(statisticType);
        String everyStatement =
                descriptinMain +
                        "\uD83D\uDFE2 Приход\n" +
                        "«Рационы» " + creditDeliverySum.intValue() + "\n" +
                        "«Десерты» " + creditDesertSum.intValue() + "\n" +
                        "\uD83D\uDD34 Расход\n" +
                        "«Рационы» " + debitDeliverySum.intValue() + "\n" +
                        "«Десерты» " + debitDesertSum.intValue() + "\n";

        return everyStatement;
    }
}
