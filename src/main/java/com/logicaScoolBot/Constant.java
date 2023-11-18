package com.logicaScoolBot;

import com.logicaScoolBot.entity.City;

import java.util.Map;

import static com.logicaScoolBot.entity.City.COMMON;
import static com.logicaScoolBot.entity.City.DUBNA;
import static com.logicaScoolBot.entity.City.MOSCOW;
import static com.logicaScoolBot.entity.City.RAMENSKOE;
import static com.logicaScoolBot.entity.City.VOSKRESENSK;

public class Constant {

    public static final Map<String, City> MAP_CITY = Map.of(
            "ДУБНА", DUBNA,
            "МОСКВА", MOSCOW,
            "ВОСКРЕСЕНСК", VOSKRESENSK,
            "РАМЕНСКОЕ", RAMENSKOE,
            "ОБЩИЙ", COMMON
    );


}
