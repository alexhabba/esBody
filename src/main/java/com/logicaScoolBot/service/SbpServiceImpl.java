package com.logicaScoolBot.service;

import com.logicaScoolBot.dto.DataDto;
import com.logicaScoolBot.dto.RequestQrRegistrationDto;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.entity.QrStatus;
import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.repository.QrRepository;
import com.logicaScoolBot.repository.StudentRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class SbpServiceImpl implements SbpService {
    private static final String URI_BASE = "https://enter.tochka.com/uapi/sbp/v1.0/";
    private static final String URI_REGISTER_QR = URI_BASE + "qr-code/merchant/MA0001860755/40802810801500302115/044525104";
    private static final String URI_GET_QRC_STATUS = URI_BASE + "qr-codes/qrcId/payment-status";

    private final HttpHeaders headers;

    private final RestTemplate restTemplate;
    private final StudentRepository studentRepository;
    private final QrRepository qrRepository;

    public SbpServiceImpl(StudentRepository studentRepository, QrRepository qrRepository) {
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        this.studentRepository = studentRepository;
        this.qrRepository = qrRepository;
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvbjJhdzBwOWE3RnlUbFBXaGtMbmJIelJZcTJ5dURobSJ9.ojsvdwVeqpRuR2wmIDP_myS7gbkq6xi1jK8t3NgXUMZo35tJtli-Hpn142QVLwJEbzKyDpMcMDaXsSbdwr690pLmHhn7TH5TvQizhFFTha6XQmpoivOZxUWzepbsc-3ggLy2UkiRWNSG_UgiWNdItv0A8oCh8hIjPDWo4_qNEFl8WR5is1mJ-rdACdIH7TERW_-udud3oBoOSTsVXiRbvqDzi7WJloo0CZMZ_9i-foSHtkf2EAbyrFB-liDCuFb56UgzFEuxfwvc2DFCTgp6QKIL_d-hatM8P8r79TWiZV5zEMgsz6NqShJNqwe6sZjegErw7aKd4_VxmtwNkHSlhmlp3jtS60TFQwSwDdIeFuVY7uOAxVZ45IijFMIdMyGsyad-LozcZBbrijON7Lz7MsLESpRBMA4ZtOG_DlP4-sdLh_rCvzAX8Y2uwUTwIdHlWna6u-rBVXSswFlfbbqs1Rs8n1ja4izYiD_xx-pArJRxqLcJbreZG6UnEhB2o6Ga");
    }

    @Override
    @Transactional
    public String registerQr(int amount, String purpose) {
        RequestQrRegistrationDto requestQrRegistrationDto = createRequestQrRegistrationDto(amount, purpose);
        HttpEntity<RequestQrRegistrationDto> entity = new HttpEntity<>(requestQrRegistrationDto, headers);
        Object body = restTemplate.exchange(URI_REGISTER_QR, HttpMethod.POST, entity, Object.class).getBody();
        LinkedHashMap<String, LinkedHashMap<String, String>> res = (LinkedHashMap<String, LinkedHashMap<String, String>>) body;
        LinkedHashMap<String, String> data = res.get("Data");
        String qrcId = data.get("qrcId");
        String payload = data.get("payload");
        Student student = studentRepository.findStudentByPhone(purpose);
        if (isNull(student)) {
            return "Нет клиента с таким номером";
        }
        Qr qr = Qr.builder()
                .qrcId(qrcId)
                .status(QrStatus.NotStarted)
                .amount(amount / 100)
                .purpose(purpose)
                .student(student)
                .build();
        student.getQrc().add(qr);

        studentRepository.saveAndFlush(student);
        return payload;
    }

    @Override
    public List<String> getQrStatus(List<String> qrcIdNotStartedList) {
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
        List<Qr> qrs = qrRepository.findAll();
        List<String> qrsNotStartedStatuses = qrs.stream()
                .filter(qr -> qr.getStatus() == QrStatus.NotStarted)
                .map(Qr::getQrcId)
                .collect(Collectors.toList());

        qrsNotStartedStatuses = getQrStatus(qrsNotStartedStatuses);
        qrRepository.updateStatuses(qrsNotStartedStatuses);

        return qrsNotStartedStatuses;
    }

    @Override
    @Transactional
    public List<Qr> getAllByQrId(List<String> qrcIds) {
        return qrRepository.findAllByQrId(qrcIds);
    }

}
