package com.es.body.enums;

import java.util.List;

public enum Role {
    SUPER_ADMIN,
    ADMIN,
    ADMIN_DUBNA,
    ADMIN_MOSKOW,
    ADMIN_VOSKRESENSK,
    ADMIN_RAMENSKOE,
    ADMIN_TEST,
    TEACHER_TEST,
    TEACHER_DUBNA,
    TRADER,
    // Бухгалтер
    ACCOUNTANT,
    MANAGER;

//    todo додумать
    public List<Role> getrl() {
        Role.values();
        return List.of();
    }
}
