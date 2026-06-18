package com.toyproject.backend.domain.post.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostTypeEnum {
    NOTICE("공지사항"),
    GENERAL("지식"),
    QNA("Q&A");

    private final String description;

    PostTypeEnum(String description) {
        this.description = description;
    }

    public static PostTypeEnum fromDescription(String description) {
        return Arrays.stream(values())
                .filter(e -> e.description.equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("없는 타입: " + description));
    }
}
