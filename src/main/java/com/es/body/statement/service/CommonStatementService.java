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

import static com.es.body.enums.Role.ACCOUNTANT;
import static com.es.body.enums.Role.ADMIN;

@Service
@RequiredArgsConstructor
public class CommonStatementService {

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
                OrgType.DELIVERY, Pair.of(accountIdDelivery, tokenDelivery)
        );
    }

    public void getStatement(OrgType orgType) {
        String accountId = MAP_RESOLVER.get(orgType).getLeft();
        String token = MAP_RESOLVER.get(orgType).getRight();
        ResponseStatementDto statement = statementService.getStatement(accountId, token, LocalDate.now().toString(), LocalDate.now().toString());
        Map<String, TransactionDto> mapPaymentIdTransactionDto = statement.getData().getStatement().stream()
                .map(ResponseStatementDto.Statement::getTransactions)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(TransactionDto::getPaymentId, x -> x));

        Set<String> paymentIds = mapPaymentIdTransactionDto.keySet();

        // получили список id которых нет в БД
        Set<String> allByPaymentIds = consumptionService.findMissingPaymentIds(paymentIds, orgType);

        List<Consumption> consumptions = new ArrayList<>();
        allByPaymentIds.forEach(transactionId -> {
            TransactionDto transactionDto = mapPaymentIdTransactionDto.get(transactionId);
            Consumption consumption = consumptionMapper.toEntity(transactionDto);
            consumption.setOrgType(orgType);
            consumptions.add(consumption);
        });

        // пока просто сохраняем
        consumptionService.saveAll(consumptions);


        // добавить отправку всем у кого роль Админ

        if (!consumptions.isEmpty()) {
            userRepository.findAllByRoles(List.of(ADMIN, ACCOUNTANT)).forEach(u ->
                consumptions.forEach(c -> {
                    senderService.send(u.getChatId(), c.getView());
                })
            );
        }
    }
}
