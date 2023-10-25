package com.example.p2k._core.security;

import com.example.p2k.user.User;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String name;

    @Builder
    public SessionUser(User user) {
        this.name = user.getName();
    }
}
