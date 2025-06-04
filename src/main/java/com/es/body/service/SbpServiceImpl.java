package com.es.body.service;

import com.es.body.dto.DataDto;
import com.es.body.dto.RequestQrRegistrationDto;
import com.es.body.entity.Qr;
import com.es.body.enums.QrStatus;
import com.es.body.entity.Client;
import com.es.body.repository.QrRepository;
import com.es.body.repository.ClientRepository;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class SbpServiceImpl implements SbpService {

    @Value("${delivery.token}")
    private String tokenDelivery;
    private static final String URI_BASE = "https://enter.tochka.com/uapi/sbp/v1.0/";

    private static final String URI_REGISTER_QR = URI_BASE + "qr-code/merchant/MA0004753994/40802810020000640637/044525104";

    private static final String URI_GET_QRC_STATUS = URI_BASE + "qr-codes/qrcId/payment-status";

    private final HttpHeaders headers;

    private final RestTemplate restTemplate;
    private final ClientRepository clientRepository;
    private final QrRepository qrRepository;

    public SbpServiceImpl(ClientRepository clientRepository, QrRepository qrRepository) {
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        this.clientRepository = clientRepository;
        this.qrRepository = qrRepository;
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", tokenDelivery);
    }

    @Override
    @Transactional
    public String registerQr(int amount, String purpose, String nameAdder) {
        try {
            RequestQrRegistrationDto requestQrRegistrationDto = createRequestQrRegistrationDto(amount, purpose);
            HttpEntity<RequestQrRegistrationDto> entity = new HttpEntity<>(requestQrRegistrationDto, headers);
            Object body = restTemplate.exchange(URI_REGISTER_QR, HttpMethod.POST, entity, Object.class).getBody();
            LinkedHashMap<String, LinkedHashMap<String, String>> res = (LinkedHashMap<String, LinkedHashMap<String, String>>) body;
            LinkedHashMap<String, String> data = res.get("Data");
            String qrcId = data.get("qrcId");
            String payload = data.get("payload");
            Client client = clientRepository.findStudentByPhone(purpose);
            if (isNull(client)) {
                return "Нет клиента с таким номером";
            }
            Qr qr = Qr.builder()
                    .qrcId(qrcId)
                    .status(QrStatus.NotStarted)
                    .amount(amount / 100)
                    .purpose(purpose)
                    .nameAdder(nameAdder)
                    .client(client)
                    .build();
            client.getQrc().add(qr);

            clientRepository.saveAndFlush(client);
            return payload;
        } catch (Exception ex) {
            System.out.println("Ошибка SbpServiceImpl.registerQr");
            return null;
        }
    }

    @Override
    @Timed("getQrStatus")
    public List<String> getQrStatus(List<String> qrcIdNotStartedList) {
        try {
            String qrsString = String.join(",", qrcIdNotStartedList);
            HttpEntity<RequestQrRegistrationDto> entity = new HttpEntity<>(headers);
            Object body = restTemplate.exchange(URI_GET_QRC_STATUS.replace("qrcId", qrsString), HttpMethod.GET, entity, Object.class).getBody();
            LinkedHashMap<String, LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>> res = (LinkedHashMap<String, LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>>) body;
            LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> data = res.get("Data");
            ArrayList<LinkedHashMap<String, String>> data1 = data.get("paymentList");
            return data1.stream()
                    .filter(el -> QrStatus.Accepted.name().equals(el.get("status")))
                    .map(el -> el.get("qrcId"))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            System.out.println("Ошибка SbpServiceImpl.getQrStatus");
            return List.of();
        }
    }

    private RequestQrRegistrationDto createRequestQrRegistrationDto(int amount, String purpose) {
        return RequestQrRegistrationDto.builder().Data(DataDto.builder()
                .merchantId("MA0001860755")
                .legalId("LA0001107006")
                .currency("RUB")
                .amount(BigDecimal.valueOf(amount))
                .paymentPurpose(purpose)
                .sourceName("string")
                .qrcType("02")
                .build()
        ).build();
    }

    @Transactional
    @Override
    public List<String> statusQr() {
        try {
            List<Qr> qrs = qrRepository.findAll();
            List<String> qrsNotStartedStatuses = qrs.stream()
                    .filter(qr -> qr.getStatus() == QrStatus.NotStarted)
                    .map(Qr::getQrcId)
                    .collect(Collectors.toList());

            qrsNotStartedStatuses = getQrStatus(qrsNotStartedStatuses);
            qrRepository.updateStatuses(qrsNotStartedStatuses, LocalDateTime.now());

            return qrsNotStartedStatuses;
        } catch (Exception ex) {
            System.out.println("Ошибка SbpServiceImpl.statusQr");
            return List.of();
        }
    }

    @Override
    @Transactional
    public List<Qr> getAllByQrId(List<String> qrcIds) {
        return qrRepository.findAllByQrId(qrcIds);
    }

}
