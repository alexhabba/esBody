package com.es.body.statement.service;

import com.es.body.entity.Consumption;
import com.es.body.enums.OrgType;
import com.es.body.mapper.ConsumptionMapper;
import com.es.body.repository.UserRepository;
import com.es.body.service.ConsumptionService;
import com.es.body.service.SenderService;
import com.es.body.statement.dto.ResponseStatementDto;
import com.es.body.statement.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.es.body.entity.Consumption.*;
import static com.es.body.enums.OrgType.DELIVERY;
import static com.es.body.enums.Role.*;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class CommonStatementService {

    // ИП БОЧКИН АЛЕКСАНДР АЛЕКСАНДРОВИЧ - делим на 3 части 1 часть на эстетику 2 на рационы 3 часть не учитываем
    private static final String RENT = "ИП БОЧКИН АЛЕКСАНДР АЛЕКСАНДРОВИЧ";
    private static final Set<String> SET_DESERT = Set.of(
            "ООО \"КОНДИТЕРСНАБ\"",
            "ООО \"ИНТЕРНЕТ РЕШЕНИЯ\""
    );

    private static final Set<String> SET_DELIVERY = Set.of(
            "ООО \"СВИТ ЛАЙФ ФУДСЕРВИС\"",
            "ООО \"МЕТРО КЭШ ЭНД КЕРРИ\"",
            "ООО \"ПОЛИМЕРТАРА\""
    );

    private static final Set<String> SET_COMMON_50_50 = Set.of(
            "ООО \"ПАКТРЕЙД\""
    );

    private Map<OrgType, Pair<String, String>> MAP_RESOLVER;
    @Value("${delivery.account}")
    private String accountIdDelivery;

    @Value("${delivery.token}")
    private String tokenDelivery;

    @Value("${desert.account}")
    private String accountIdDesert;

    @Value("${desert.token}")
    private String tokenDesert;

    private final StatementService statementService;
    private final ConsumptionService consumptionService;
    private final ConsumptionMapper consumptionMapper;
    private final UserRepository userRepository;
    private final SenderService senderService;

    @PostConstruct
    void init() {
        MAP_RESOLVER = Map.of(
                OrgType.DESERT, Pair.of(accountIdDesert, tokenDesert),
                DELIVERY, Pair.of(accountIdDelivery, tokenDelivery)
        );
    }

    public void getStatement(OrgType orgType) {
        String accountId = MAP_RESOLVER.get(orgType).getLeft();
        String token = MAP_RESOLVER.get(orgType).getRight();
        ResponseStatementDto statement = statementService.getStatement(accountId, token, LocalDate.now().toString(), LocalDate.now().toString());
        Map<String, TransactionDto> mapPaymentIdTransactionDto = statement.getData().getStatement().stream()
                .map(ResponseStatementDto.Statement::getTransactions)
                .flatMap(Collection::stream)
                .filter(t -> nonNull(t.getPaymentId()))
                .peek(this::getSetPaymentId)
                .collect(Collectors.toMap(TransactionDto::getPaymentId, x -> x));

        Set<String> paymentIds = mapPaymentIdTransactionDto.keySet();

        // получили список id которых нет в БД
        Set<String> allByPaymentIds = consumptionService.findMissingPaymentIds(paymentIds);

        List<Consumption> consumptions = new ArrayList<>();
        allByPaymentIds.forEach(transactionId -> {
            TransactionDto transactionDto = mapPaymentIdTransactionDto.get(transactionId);
            Consumption consumption = consumptionMapper.toEntity(transactionDto);
            consumption.setOrgType(getOrgType(consumption.getDescription(), orgType));
            consumptions.add(consumption);
        });

        consumptionService.saveAll(consumptions);

        // не отправляем если есть вхождение этого "Комиссия за зачисление перевода по QR"
        List<Consumption> consumptionFiltered = consumptions.stream()
                .filter(this::isNotCommissionQr)
                .collect(Collectors.toList());

        if (!consumptionFiltered.isEmpty()) {
            userRepository.findAllByRoles(List.of(ADMIN_TEST, SUPER_ADMIN, ACCOUNTANT)).forEach(u ->
                            consumptionFiltered.forEach(c -> {
                    senderService.send(u.getChatId(), c.getView());
                })
            );
        }
    }

    /**
     * Тут проходят проверки и распределение финансов по организациям
     */
    private void getSetPaymentId(TransactionDto t) {
//        if (t.getN)
        t.setPaymentId(t.getPaymentId() + t.getCreditDebitIndicator());
    }

    private boolean isNotCommissionQr(Consumption c) {
        return !c.getDescription().startsWith(COMMISSION_ON_QR);
    }

    private OrgType getOrgType(String description, OrgType orgType) {
        if(description.contains(PYATEROCHKA) && description.contains(BUY)) {
            return DELIVERY;
        }
        else {
            return orgType;
        }
    }
}


// Рационы
// ООО "СВИТ ЛАЙФ ФУДСЕРВИС"
// ООО "МЕТРО КЭШ ЭНД КЕРРИ"
// ООО "ПОЛИМЕРТАРА"

// десерты
// ООО "КОНДИТЕРСНАБ"

// ИП БОЧКИН АЛЕКСАНДР АЛЕКСАНДРОВИЧ - делим на 3 части 1 часть на эстетику 2 на рационы 3 часть не учитываем
// ООО "ПАКТРЕЙД" 50 на 50
// ООО "ИНТЕРНЕТ РЕШЕНИЯ" десерты

//Покупка товара(Терминал:PYATEROCHKA,PASS SADOVYY 2-Y 2,Penza,RU,дата операции:22/06/2025 08:30(МСК),на сумму:328.94 RUB,карта 2204********1052)
//Покупка товара(Терминал:PYATEROCHKA,PASS SADOVYY 2-Y 2,Penza,RU,дата операции:23/06/2025 08:56(МСК),на сумму:3715.03 RUB,карта 2204********1052)
//Покупка товара(Терминал:SP_SPAR ONLINE 55,PR-T STROITELEY 40,Penza,RU,дата операции:22/06/2025 10:35(МСК),на сумму:1678.8 RUB,карта 2204********1052)

