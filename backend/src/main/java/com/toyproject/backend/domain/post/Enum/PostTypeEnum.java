package com.toyproject.backend.domain.post.Enum;

import lombok.Getter;

@Getter
public enum PostTypeEnum {
    NOTICE("공지사항"),
    GENERAL("지식"),
    QNA("Q&A");
//
    //
    private final String description;
//
    PostTypeEnum(String description) {
        this.description = description;
    }

}
