package com.logicaScoolBot.constnt;

import com.logicaScoolBot.enums.City;

import java.util.Map;

import static com.logicaScoolBot.enums.City.COMMON;
import static com.logicaScoolBot.enums.City.DUBNA;
import static com.logicaScoolBot.enums.City.MOSCOW;
import static com.logicaScoolBot.enums.City.RAMENSKOE;
import static com.logicaScoolBot.enums.City.VOSKRESENSK;

public class Constant {

    public static final String QR_GENERATE = "Сгенерировать QR";
    public static final String ADD_NEW_STUDENT = "Добавление нового ученика";
    public static final String STARTED_WORK = "Приступил к работе";
    public static final String END_LESSON = "Закончил урок";

    public static final Map<String, City> MAP_CITY = Map.of(
            "ДУБНА", DUBNA,
            "МОСКВА", MOSCOW,
            "ВОСКРЕСЕНСК", VOSKRESENSK,
            "РАМЕНСКОЕ", RAMENSKOE,
            "ОБЩИЙ", COMMON
    );


}
