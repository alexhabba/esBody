package com.logicaScoolBot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final String mosckow =
            "Чегоданова 15000\n" +
                    "Ольга 11750\n" +
                    "Малышев 13800\n" +
                    "Санакоев 9000\n" +
                    "Чеснакова 8980\n" +
                    "Соловьева 4500\n";

    private static final String dubna =
            "Колисниченко 17280\n" +
                    "Айсун 11280\n" +
                    "Гультаева 21400\n" +
                    "Кудряшов 22960\n" +
                    "Кудрявцев 4400\n" +
                    "Хлынов 15760\n" +
                    "Лукин 8730\n" +
                    "Колисниченко 5720\n" +
                    "Красова 2920\n" +
                    "Грошев 6400\n" +
                    "Трошкина 4440";

    private static final String summury =
            "Скалкина 16800\n" +
                    "Чегоданова 15000\n" +
                    "Ольга 11750\n" +
                    "Малышев 13800\n" +
                    "Санакоев 9000\n" +
                    "Чеснакова 8980\n" +
                    "Соловьева 4500\n" +
                    "\n" +
                    "Дерека 30900\n" +
                    "Мишуто 3300\n" +
                    "Попов 4600\n" +
                    "Швец 600\n" +
                    "\n" +
                    "Кривенко 24770\n" +
                    "Дорофеева 6480\n" +
                    "Коржавин 1200\n" +
                    "Желтухин 600\n" +
                    "Кузьминых 1200\n" +
                    "Мостуненко 3900\n" +
                    "Писарченко 6600\n" +
                    "Князев 2400\n" +
                    "\n" +
                    "Колисниченко 17280\n" +
                    "Айсун 11280\n" +
                    "Гультаева 21400\n" +
                    "Кудряшов 22960\n" +
                    "Кудрявцев 4400\n" +
                    "Хлынов 15760\n" +
                    "Лукин 8730\n" +
                    "Колисниченко 5720\n" +
                    "Красова 2920\n" +
                    "Грошев 6400\n" +
                    "Трошкина 4440";

    public static void main(String[] args) {
//        посчитать колличество каждого вида логов
//        и вывести реззультат в порядке убывания  важности

//        найти поток причину письма девопса
//        посчитать для него было сколько каждого видов логов и вывести в порядке задаваемом пользователя
        List<String> logs = List.of(
                "debug [pull a thread -1]  warning sdf123",
                "warning [pull a thread -3]  warning sdf123",
                "error [pull a thread -1]  warning sdf123",
                "info [pull a thread -3]  warning sdf123",
                "debug [pull a thread -1]  warning sdf123",
                "debug [pull a thread -2]  warning sdf123",
                "info [pull a thread -1]  warning sdf123",
                "info [pull a thread -3]  warning sdf123",
                "debug [pull a thread -2]  warning sdf123",
                "error [pull a thread -3]  warning sdf123",
                "warning [pull a thread -1]  warning sdf123",
                "debug [pull a thread -1]  warning sdf123",
                "error [pull a thread -2]  warning sdf123"
        );

//        Map<String, Integer> collect = logs.stream()
//                .map(str -> str.split("\\s+"))
//                .map(strings -> strings[0])
//                .collect(Collectors.toMap(s -> s, s -> 1, (k, v) -> v + 1));

        Map<String, Long> collect = logs.stream()
                .map(str -> str.split("\\s+"))
                .collect(Collectors.groupingBy(s -> s[0], Collectors.counting()));

        System.out.println(collect);
//        System.out.println();
//
//        String str = "dfvddfv erdfergfergf ";
//        System.out.println(str.trim());
//        getSum();
//
//        String s = "12-9-23";
//        String s1 = getLocalDateBySlash(s, "-");
//        System.out.println(s1);
//
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//        System.out.println(LocalDate.parse(s1, dateTimeFormatter));
//        System.out.println(LocalDate.parse("12/09/2023", dateTimeFormatter));
//        System.out.println(LocalDate.parse("12/9/2023", dateTimeFormatter));
        System.out.println(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    private static Integer getSum() {
        int sum = Arrays.stream(summury.split("\\s+"))
                .mapToInt(s -> {
                    try {
                        return Integer.valueOf(s);
                    } catch (Exception e) {
                        return 0;
                    }
                }).sum();
//                .collect(Collectors.summingInt());
        System.out.println(sum);
        return 0;
    }

    private static String getLocalDateBySlash(String date, String regex) {
        String[] splitDate = date.split(regex);
        if (splitDate[2].length() == 2) {
            date = date.substring(0, date.length() - 2) + "20" + date.substring(date.length() - 2);
        }
        if (splitDate[0].length() == 1) {
            date = "0" + date;
        }
        if (splitDate[1].length() == 1) {
            date = date.substring(0, 3) + "0" + date.substring(3);
        }
        return date;
    }
}