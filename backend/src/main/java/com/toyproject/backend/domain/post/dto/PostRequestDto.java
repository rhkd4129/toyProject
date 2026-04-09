package com.toyproject.backend.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 10, message = "내용은 10자 이상이어야 합니다")
    private String content;

    @NotBlank(message = "게시글 유형은 필수입니다")
    private String postType; // "NOTICE", "GENERAL", "QNA"
}
