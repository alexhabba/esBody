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
import org.springframework.data.util.Pair;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

    public static final String COMMISSION_ON_QR = "Комиссия за зачисление перевода по QR";
    private static final String PAYMENT = "Зачисление по QR";
    public static final String PYATEROCHKA = "PYATEROCHKA";
    public static final String BUY = "Покупка";

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
        String desc = description.concat((nonNull(name) ? " ".concat(name) : "")).startsWith(COMMISSION_ON_QR) ? COMMISSION_ON_QR : description.concat((nonNull(name) ? " ".concat(name) : ""));
        desc = desc.startsWith(PAYMENT) ? PAYMENT : desc;
        desc = desc.contains(PYATEROCHKA) && desc.contains(BUY) ? PYATEROCHKA : desc;

        return (creditDebit == CreditDebitIndicator.Credit ? "\uD83D\uDFE2 Приход " : "\uD83D\uDD34 Расход ") +
                amount +
                (orgType == OrgType.DELIVERY ? " \"Рационы\"\n" : " \"Десерты\"\n") + desc;
    }

//    public String everyDayStatement() {
//        double debitDeliverySum = 0;
//        double debitDesertSum = 0;
//        double creditDeliverySum = 0;
//        double creditDesertSum = 0;
//        Map<String, Pair<String, String>> map = new HashMap<>();
//
//        String everyStatement =
//                "Ежедневный отчет.\n" +
//                "\uD83D\uDD34 Расход " +
//                (orgType == OrgType.DELIVERY ? " «Рационы» " : " «Десерты» ") + ;
//
//        return "";
//    }
//
////    Расход 606.65 "Десерты"
////    Покупка товара(Терминал:PYATEROCHKA,PASS SADOVYY 2-Y 2,Penza,RU,дата операции:03/06/2025 10:08(МСК),на сумму:606.65 RUB,карта 2204********1052) ООО "Банк Точка"
//
////    Ежедневно
////    Сумма оплат за текущий день 20000
////            «Рационы» 19 730
////            «Десерты» 19 730
////
////    Расход за текущий день 30000
////            "«Рационы» 10000
////            "«Десерты» 1000
////
////
////    Каждое 15 число месяца
////    Сумма оплат за текущий месяц 20000
////            «Доставка» 19 730
////            «Десерты» 19 730
////
////    Расход за текущий месяц 30000
////            «Доставка» 10000
////            «Десерты» 1000
////
////    Прибыль за текущий месяц 30000
////            «Доставка» 10000
////            «Десерты» 1000
////
////
////    Каждый последний день месяца
////    Сумма оплат за текущий месяц 20000
////            «Доставка» 19 730
////            «Десерты» 19 730
////
////    Расход за текущий месяц 30000
////            «Доставка» 10000
////            «Десерты» 1000
////
////    Прибыль за текущий месяц 30000
////            «Доставка» 10000
////            «Десерты» 1000
}
