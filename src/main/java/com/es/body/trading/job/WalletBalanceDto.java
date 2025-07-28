package com.es.body.trading.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceDto {


    private int retCode;
    private String retMsg;
    private Result result;
    private Object retExtInfo; // Assuming retExtInfo is empty
    private long time;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private List<WalletData> list;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalletData {
        private BigDecimal totalEquity;
        private String accountIMRate;
        private BigDecimal totalMarginBalance;
        private BigDecimal totalInitialMargin;
        private String accountType;
        private BigDecimal totalAvailableBalance;
        private String accountMMRate;
        private BigDecimal totalPerpUPL;
        private BigDecimal totalWalletBalance;
        private String accountLTV;
        private BigDecimal totalMaintenanceMargin;
        private List<CoinData> coin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoinData {
        private BigDecimal availableToBorrow;
        private BigDecimal bonus;
        private BigDecimal accruedInterest;
        private BigDecimal availableToWithdraw;
        private BigDecimal totalOrderIM;
        private BigDecimal equity;
        private BigDecimal totalPositionMM;
        private BigDecimal usdValue;
        private BigDecimal spotHedgingQty;
        private BigDecimal unrealisedPnl;
        private boolean collateralSwitch;
        private BigDecimal borrowAmount;
        private BigDecimal totalPositionIM;
        private BigDecimal walletBalance;
        private BigDecimal cumRealisedPnl;
        private BigDecimal locked;
        private boolean marginCollateral;
        private String coin;
    }
}