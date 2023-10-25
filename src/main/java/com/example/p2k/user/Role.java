package com.example.p2k.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_STUDENT("학생"),
    ROLE_INSTRUCTOR("교육자"),
    ROLE_ADMIN("관리자");
    private String value;
}
