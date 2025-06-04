package com.es.body.statement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {

    private String transactionId;
    private String paymentId;
    // Credit - приход, Debit - расход
    private String creditDebitIndicator;
    private String status;
    private String documentNumber;
    private String transactionTypeCode;
    private String documentProcessDate;
    private String description;

    @JsonProperty("Amount")
    private Amount amount;

    @JsonProperty("CreditorParty")
    private Party creditorParty;

    @JsonProperty("CreditorAccount")
    private Account creditorAccount;

    @JsonProperty("CreditorAgent")
    private Agent creditorAgent;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private BigDecimal amount;
        private BigDecimal amountNat;
        private String currency;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Party {
        private String inn;
        private String name;
        private String kpp;

    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
        private String schemeName;
        private String identification;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Agent {
        private String schemeName;
        private String identification;
        private String accountIdentification;
        private String name;
    }
}

