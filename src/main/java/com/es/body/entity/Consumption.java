package com.es.body.entity;

import com.es.body.enums.CreditDebitIndicator;
import com.es.body.enums.OrgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.nonNull;

/**
 * Таблица расходов.
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consumption {

    private static final String COMMISSION = "Комиссия за зачисление перевода по QR";
    private static final String PAYMENT = "Зачисление по QR";

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String transactionId;

    private String paymentId;

    private String name;

    @Enumerated(EnumType.STRING)
    private OrgType orgType;

    @Enumerated(EnumType.STRING)
    private CreditDebitIndicator creditDebit;

    private String description;

    private BigDecimal amount;

    private boolean isSend;

    @CreationTimestamp
    private LocalDateTime createDate;

    public String getView() {
        String desc = description.concat((nonNull(name) ? " ".concat(name) : "")).startsWith(COMMISSION) ? COMMISSION : description.concat((nonNull(name) ? " ".concat(name) : ""));
        desc = desc.startsWith(PAYMENT) ? PAYMENT : desc;

        return (creditDebit == CreditDebitIndicator.Credit ? "\uD83D\uDFE2 Приход " : "\uD83D\uDD34 Расход ") +
                amount +
                (orgType == OrgType.DELIVERY ? " \"Доставка\"\n" : " \"Десерты\"\n") + desc;
    }
}
