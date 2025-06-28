package com.es.body.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    @JsonProperty("Data")
    private Data data;
    private Links links;
    private Meta meta;

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        @JsonProperty("Balance")
        private List<Balance> balance;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Balance {
        private String accountId;
        private String creditDebitIndicator; // Можно использовать enum
        private String type; // Можно использовать enum
        private OffsetDateTime dateTime;
        @JsonProperty("Amount")
        private Amount amount;

        @lombok.Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Amount {
            private double amount;
            private String currency; // Можно использовать java.util.Currency
        }
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Links {
        private String self;
    }

    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private int totalPages;
    }
}
