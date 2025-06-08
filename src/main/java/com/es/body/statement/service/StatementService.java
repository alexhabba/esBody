package com.es.body.statement.service;

import com.es.body.statement.dto.RequestResponseCreateStatementDto;
import com.es.body.statement.dto.ResponseStatementDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

import static com.es.body.statement.Utils.getResponse;
import static com.es.body.statement.Utils.postResponse;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public ResponseStatementDto getStatement(String accountId, String token, String startDate, String endDate) {
        String statementId = createStatementAndGetStatementId(startDate, endDate, accountId, token);

        Thread.sleep(10000);

        return getStatement(accountId, token, statementId);
    }

    @SneakyThrows
    public String createStatementAndGetStatementId(String startDate, String endDate, String accountId, String token) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestResponseCreateStatementDto requestResponseCreateStatementDto = createRequest(startDate, endDate, accountId);

        RequestBody body = RequestBody.create(mediaType, objectMapper.writeValueAsString(requestResponseCreateStatementDto));
//        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"Data\": {\r\n        \"Statement\": {\r\n            \"accountId\": \"40802810020000640637/044525104\",\r\n            \"startDateTime\": \"2025-05-21\",\r\n            \"endDateTime\": \"2025-05-21\"\r\n        }\r\n    }\r\n}");
        Request request = new Request.Builder()
                .url("https://enter.tochka.com/uapi/open-banking/v1.0/statements")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Mozilla/5.0")  // Обязательный заголовок!
                .addHeader("Authorization", token)
                .build();

        String response = postResponse(request, 3);

        return objectMapper.readValue(response, RequestResponseCreateStatementDto.class).getData().getStatement().getStatementId();
    }

    public ResponseStatementDto getStatement(String accountId, String token, String statementId) throws IOException {
        Request request = new Request.Builder()
                .url("https://enter.tochka.com/uapi/open-banking/v1.0/accounts/" + accountId + "/statements/" + statementId)
                .addHeader("Authorization", token)
                .build();
        String response = getResponse(request, 3);
        return objectMapper.readValue(response, ResponseStatementDto.class);
    }

    private RequestResponseCreateStatementDto createRequest(String startDate, String endDate, String accountId) {
        return RequestResponseCreateStatementDto.builder().data(RequestResponseCreateStatementDto.Data.builder().statement(RequestResponseCreateStatementDto.Statement.builder()
                .startDateTime(startDate).endDateTime(endDate).accountId(accountId).build()).build()).build();
    }
}
