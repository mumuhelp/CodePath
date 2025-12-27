package ru.volchari.codepath.model;

import lombok.Getter;

public enum Provider {
    GITHUB("github"),
    GOOGLE("google"),
    LOCAL("local");

    @Getter
    private final String name;

    Provider(String name) {
        this.name = name;
    }

}
