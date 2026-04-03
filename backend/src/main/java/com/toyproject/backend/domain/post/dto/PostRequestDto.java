package com.toyproject.backend.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private String postType; // "NOTICE", "GENERAL", "QNA"
}
