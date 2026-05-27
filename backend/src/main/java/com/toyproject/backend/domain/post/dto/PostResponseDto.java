package com.toyproject.backend.domain.post.dto;

import com.toyproject.backend.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final Integer likes;
    private final Integer dislikes;
    private final String postType;
    private final LocalDateTime createdAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.likes = post.getLikes();
        this.dislikes = post.getDislikes();
        this.postType = post.getPostType().name();
        this.createdAt = post.getCreatedAt();
    }
}
