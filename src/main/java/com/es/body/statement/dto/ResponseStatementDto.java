package com.es.body.statement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseStatementDto {

    @JsonProperty("Data")
    private Data data;

    @Builder
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        @JsonProperty("Statement")
        private List<Statement> statement;
    }

    @Builder
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statement {
        private String accountId;
        private String startDateTime;
        private String endDateTime;
        private String statementId;
        @JsonProperty("Transaction")
        private List<TransactionDto> transactions;
    }
}


