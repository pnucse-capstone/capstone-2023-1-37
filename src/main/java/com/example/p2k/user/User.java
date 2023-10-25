package com.example.p2k.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_tb")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 256)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // 학생, 교육자, 관리자

    private Boolean pending; //승인 여부

    @Builder
    public User(Long id, String email, String name, String password, Role role, Boolean pending) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.pending = pending;
    }

    public void updatePending(boolean status){
        this.pending = status;
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void update(String email, String name){
        this.email = email;
        this.name = name;
    }
}