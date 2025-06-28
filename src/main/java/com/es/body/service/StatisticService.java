package com.es.body.service;

import com.es.body.entity.Consumption;
import com.es.body.enums.OrgType;
import com.es.body.enums.StatisticType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import static com.es.body.trading.CandleService.formatWithSpaces;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class StatisticService {

    @Value("${delivery.account}")
    private String accountIdDelivery;

    @Value("${delivery.token}")
    private String tokenDelivery;

    @Value("${desert.account}")
    private String accountIdDesert;

    @Value("${desert.token}")
    private String tokenDesert;

    private static final Map<StatisticType, String> MAP_STATISTIC_TYPE_DESCRIPTION =
            Map.of(
                    DAY, "Ежедневный отчет.\n",
                    MEDIAN_MONTH, "Отчет за пол месяца.\n",
                    MONTH, "Отчет за месяц.\n"
            );

    private final BalanceInfoService balanceInfoService;

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

        String descriptionMain = MAP_STATISTIC_TYPE_DESCRIPTION.get(statisticType);
        String everyStatement =
                descriptionMain +
                        "\uD83D\uDFE2 Приход\n" +
                        "«Рационы» " + formatWithSpaces(creditDeliverySum.intValue()) + "\n" +
                        "«Десерты» " + formatWithSpaces(creditDesertSum.intValue()) + "\n\n" +

                        "\uD83D\uDD34 Расход\n" +
                        "«Рационы» " + formatWithSpaces(debitDeliverySum.intValue()) + "\n" +
                        "«Десерты» " + formatWithSpaces(debitDesertSum.intValue()) + "\n\n" +

                        "\uD83D\uDCAA Остаток на счетах\n" +
                        "«Рационы» " + formatWithSpaces(Double.doubleToLongBits(balanceInfoService.getBalance(accountIdDelivery, tokenDelivery))) + "\n" +
                        "«Десерты» " + formatWithSpaces(Double.doubleToLongBits(balanceInfoService.getBalance(accountIdDesert, tokenDesert))) + "\n\n";

        return everyStatement;
    }
}
