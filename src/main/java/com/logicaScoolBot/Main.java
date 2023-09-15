package com.logicaScoolBot;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
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

        Map<String, Integer> collect = logs.stream()
                .map(str -> str.split("\\s+"))
                .map(strings -> strings[0])
                .collect(Collectors.toMap(s -> s, s -> 1, (k, v) -> v + 1));

        System.out.println();

        String str = "dfvddfv erdfergfergf ";
        System.out.println(str.trim());
    }
}
