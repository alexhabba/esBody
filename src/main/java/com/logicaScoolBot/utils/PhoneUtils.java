package com.logicaScoolBot.utils;

public class PhoneUtils {

    public static String getPhoneFormat(String phone) {
        phone = phone.replace("+", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "");
        return "7".equals(phone.substring(0, 1)) || "8".equals(phone.substring(0, 1)) ? phone.substring(1) : phone;
    }
}
