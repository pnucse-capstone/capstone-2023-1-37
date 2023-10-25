package com.example.p2k.post;

public enum Scope {
    PUBLIC("전체 공개"), PRIVATE("교육자 공개");

    private final String description;

    Scope(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}